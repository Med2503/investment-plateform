
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

        try (ReportLeaseHeartbeat.Heartbeat heartbeat =
                     leaseHeartbeat.start(reportId, token)) {

            if (!leaseService.renew(reportId, token)) {
                log.warn(
                        "Could not renew lease before generating report {}",
                        reportId
                );
                return;
            }

            ReportFileGenerator generator =
                    selectGenerator(report.getFormat());

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
                    storageCleanupService.deleteIfPresent(
                            uploadedObjectKey,
                            reportId.toString()
                    );
                    uploadedObjectKey = null;
                    return;
                }

                boolean completed = finalizationService.complete(
                        reportId,
                        token,
                        uploadedObjectKey
                );

                if (!completed) {
                    log.warn(
                            "Report {} could not be finalized by this worker",
                            reportId
                    );

                    storageCleanupService.deleteIfPresent(
                            uploadedObjectKey,
                            reportId.toString()
                    );

                    uploadedObjectKey = null;
                    return;
                }

                log.info(
                        "Report {} completed successfully",
                        reportId
                );
            }
        } catch (Exception exception) {
            log.error(
                    "Failed to process report {}",
                    reportId,
                    exception
            );

            try {
                boolean markedFailed = finalizationService.fail(
                        reportId,
                        token,
                        "Report generation or storage failed"
                );

                if (!markedFailed && uploadedObjectKey != null) {
                    storageCleanupService.deleteIfPresent(
                            uploadedObjectKey,
                            reportId.toString()
                    );
                }
            } catch (Exception finalizationException) {
                log.error(
                        "Could not finalize failure for report {}",
                        reportId,
                        finalizationException
                );

                if (uploadedObjectKey != null) {
                    storageCleanupService.deleteIfPresent(
                            uploadedObjectKey,
                            reportId.toString()
                    );
                }
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
}
