package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.repository.ReportRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportProcessingScheduler {

    private static final int BATCH_SIZE = 20;

    private final ReportRepository reportRepository;
    private final ReportLeaseService leaseService;
    private final ReportProcessingService processingService;

    @Scheduled(
            fixedDelayString = "${reporting.processing.fixed-delay-ms:5000}"
    )
    public void processPendingReports() {
        List<UUID> reportIds = reportRepository
                .findByStatusOrderByCreatedAtAsc(
                        ReportStatus.PENDING,
                        PageRequest.of(0, BATCH_SIZE)
                )
                .getContent()
                .stream()
                .map(ReportEntity::getId)
                .toList();

        for (UUID reportId : reportIds) {
            try {
                leaseService.claimOrRecover(reportId)
                        .ifPresent(token ->
                                processingService.processClaimedReport(
                                        reportId,
                                        token
                                )
                        );
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