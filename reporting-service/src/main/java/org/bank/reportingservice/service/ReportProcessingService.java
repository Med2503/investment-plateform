package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.exception.ReportNotFoundException;
import org.bank.reportingservice.exception.ReportProcessingException;
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
    private final Clock clock;

    public void processClaimedReport(UUID reportId) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        if (report.getStatus() != ReportStatus.PROCESSING) {
            log.warn(
                    "Report is not in PROCESSING state: reportId={}, status={}",
                    reportId,
                    report.getStatus()
            );
            return;
        }

        try {
            ReportFileGenerator generator = selectGenerator(report.getFormat());

            try (GeneratedReport generated = generator.generate(report)) {
                String objectKey = createObjectKey(
                        report.getId(),
                        generated.fileExtension()
                );

                String storedKey = reportStorage.store(
                        objectKey,
                        generated.content(),
                        generated.contentLength(),
                        generated.contentType()
                );

                report.markCompleted(storedKey, Instant.now(clock));
                reportRepository.save(report);

                log.info(
                        "Report generated successfully: reportId={}, format={}",
                        reportId,
                        report.getFormat()
                );
            }
        } catch (Exception exception) {
            markFailed(reportId, exception);
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

    private void markFailed(UUID reportId, Exception exception) {
        log.error(
                "Report processing failed: reportId={}",
                reportId,
                exception
        );

        reportRepository.findById(reportId).ifPresent(report -> {
            if (report.getStatus() != ReportStatus.PROCESSING) {
                return;
            }

            String errorMessage = exception instanceof ReportProcessingException
                    ? exception.getMessage()
                    : "Report generation or storage failed";

            report.markFailed(errorMessage, Instant.now(clock));
            reportRepository.save(report);
        });
    }
}