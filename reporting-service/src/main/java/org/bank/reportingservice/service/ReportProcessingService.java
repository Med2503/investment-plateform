package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.exception.ReportNotFoundException;
import org.bank.reportingservice.model.ReportFormat;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
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
    private final Clock clock;

    public void processClaimedReport(UUID reportId, UUID token) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        if (report.getStatus() != ReportStatus.PROCESSING
                || !token.equals(report.getProcessingToken())) {
            log.warn(
                    "Report lease no longer belongs to this worker: reportId={}",
                    reportId
            );
            return;
        }

        String uploadedObjectKey = null;

        try {
            if (!leaseService.renew(reportId, token)) {
                return;
            }

            ReportFileGenerator generator = selectGenerator(
                    report.getFormat()
            );

            try (GeneratedReport generated = generator.generate(report)) {
                if (!leaseService.renew(reportId, token)) {
                    return;
                }

                String objectKey = createObjectKey(
                        report.getId(),
                        generated.fileExtension()
                );

                uploadedObjectKey = reportStorage.store(
                        objectKey,
                        generated.content(),
                        generated.contentLength(),
                        generated.contentType()
                );

                boolean completed = finalizationService.complete(
                        reportId,
                        token,
                        uploadedObjectKey
                );

                if (!completed) {
                    log.warn(
                            "Report lease expired before finalization: reportId={}",
                            reportId
                    );

                    try {
                        reportStorage.delete(uploadedObjectKey);
                    } catch (RuntimeException cleanupException) {
                        log.error(
                                "Orphaned S3 object cleanup failed: reportId={}",
                                reportId,
                                cleanupException
                        );
                    }

                    return;
                }

                log.info(
                        "Report generated successfully: reportId={}, format={}",
                        reportId,
                        report.getFormat()
                );
            }
        } catch (Exception exception) {
            log.error(
                    "Report processing failed: reportId={}",
                    reportId,
                    exception
            );

            boolean markedFailed = finalizationService.fail(
                    reportId,
                    token,
                    "Report generation or storage failed"
            );

            if (!markedFailed && uploadedObjectKey != null) {
                try {
                    reportStorage.delete(uploadedObjectKey);
                } catch (RuntimeException cleanupException) {
                    log.error(
                            "Orphaned S3 object cleanup failed: reportId={}",
                            reportId,
                            cleanupException
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
        return "reports/" + reportId + "/" + UUID.randomUUID()
                + "." + extension;
    }
}