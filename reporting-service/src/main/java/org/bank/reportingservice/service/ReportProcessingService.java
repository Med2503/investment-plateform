package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.model.ReportFormat;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportProcessingService {

    private final ReportRepository reportRepository;
    private final ReportStorage reportStorage;
    private final CsvReportGenerator csvReportGenerator;
    private final JsonReportGenerator jsonReportGenerator;
    private final PdfReportGenerator pdfReportGenerator;
    private final ReportFinalizationService finalizationService;
    private final ReportLeaseService leaseService;
    private final ReportLeaseHeartbeat leaseHeartbeat;
    private final ReportStorageCleanupService storageCleanupService;

    public void processClaimedReport(UUID reportId, UUID token) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Report not found: " + reportId
                ));

        if (report.getStatus() != ReportStatus.PROCESSING
                || !token.equals(report.getProcessingToken())) {
            log.warn(
                    "Report {} is no longer owned by this worker",
                    reportId
            );
            return;
        }

        String uploadedObjectKey = null;
        boolean completedSuccessfully = false;
        boolean finalizationAttempted = false;

        try (ReportLeaseHeartbeat.Heartbeat heartbeat =
                     leaseHeartbeat.start(reportId, token)) {

            if (!leaseService.renew(reportId, token)) {
                log.warn(
                        "Could not renew lease before processing report {}",
                        reportId
                );
                return;
            }

            ReportFileGenerator generator = selectGenerator(
                    report.getFormat()
            );

            try (GeneratedReport generated = generator.generate(report)) {
                if (heartbeat.leaseLost().get()) {
                    log.warn(
                            "Lease lost during generation of report {}",
                            reportId
                    );
                    return;
                }

                if (!leaseService.renew(reportId, token)) {
                    log.warn(
                            "Lease expired before upload for report {}",
                            reportId
                    );
                    return;
                }

                String objectKey = createObjectKey(
                        reportId,
                        generated.fileExtension()
                );

                uploadedObjectKey = reportStorage.store(
                        objectKey,
                        generated.content(),
                        generated.contentLength(),
                        generated.contentType()
                );

                if (heartbeat.leaseLost().get()) {
                    cleanupUploadedObject(uploadedObjectKey, reportId);
                    uploadedObjectKey = null;
                    return;
                }

                finalizationAttempted = true;

                boolean finalized = finalizationService.complete(
                        reportId,
                        token,
                        uploadedObjectKey
                );

                if (!finalized) {
                    log.warn(
                            "Report {} could not be finalized by this worker",
                            reportId
                    );

                    cleanupUploadedObject(uploadedObjectKey, reportId);
                    uploadedObjectKey = null;
                    return;
                }

                completedSuccessfully = true;

                log.info(
                        "Report {} completed successfully",
                        reportId
                );
            }
        } catch (Exception exception) {
            log.error(
                    "Error while processing report {}: exceptionType={}",
                    reportId,
                    exception.getClass().getSimpleName(),
                    exception
            );

            if (completedSuccessfully) {
                log.warn(
                        "Report {} was already finalized successfully. "
                                + "Its stored artifact will be preserved.",
                        reportId
                );
                return;
            }

            if (finalizationAttempted) {
                log.error(
                        "Report {} finalization outcome is uncertain. "
                                + "Preserving the uploaded artifact to avoid "
                                + "deleting a potentially finalized report.",
                        reportId
                );
                return;
            }

            try {
                finalizationService.fail(
                        reportId,
                        token,
                        "Report generation or storage failed"
                );
            } catch (Exception finalizationException) {
                log.error(
                        "Could not mark report {} as failed: exceptionType={}",
                        reportId,
                        finalizationException.getClass().getSimpleName(),
                        finalizationException
                );
            }

            if (uploadedObjectKey != null) {
                cleanupUploadedObject(uploadedObjectKey, reportId);
            }
        }
    }

    private ReportFileGenerator selectGenerator(ReportFormat format) {
        return switch (format) {
            case CSV -> csvReportGenerator;
            case JSON -> jsonReportGenerator;
            case PDF -> pdfReportGenerator;
        };
    }

    private String createObjectKey(UUID reportId, String extension) {
        return "reports/"
                + reportId
                + "/"
                + UUID.randomUUID()
                + "."
                + extension;
    }

    private void cleanupUploadedObject(
            String objectKey,
            UUID reportId
    ) {
        boolean cleanupHandled = storageCleanupService.deleteIfPresent(
                objectKey,
                reportId.toString()
        );

        if (!cleanupHandled) {
            log.error(
                    "Report artifact cleanup is unresolved: reportId={}",
                    reportId
            );
        }
    }
}
