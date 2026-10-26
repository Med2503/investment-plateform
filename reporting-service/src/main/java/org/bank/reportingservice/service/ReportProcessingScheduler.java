package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.repository.ReportRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportProcessingScheduler {

    private static final int BATCH_SIZE = 20;

    private final ReportRepository reportRepository;
    private final ReportClaimService reportClaimService;
    private final ReportProcessingService reportProcessingService;

    @Scheduled(
            fixedDelayString = "${reporting.processing.fixed-delay-ms:5000}"
    )
    public void processPendingReports() {
        List<UUID> pendingReportIds = reportRepository
                .findByStatusOrderByCreatedAtAsc(
                        ReportStatus.PENDING,
                        PageRequest.of(0, BATCH_SIZE)
                )
                .getContent()
                .stream()
                .map(ReportEntity::getId)
                .toList();

        for (UUID reportId : pendingReportIds) {
            try {
                if (reportClaimService.claim(reportId)) {
                    reportProcessingService.processClaimedReport(reportId);
                }
            } catch (Exception exception) {
                log.error(
                        "Unable to process report: reportId={}",
                        reportId,
                        exception
                );
            }
        }
    }
}