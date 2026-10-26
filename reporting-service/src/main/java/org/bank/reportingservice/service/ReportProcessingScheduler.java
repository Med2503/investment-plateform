package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.repository.ReportRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportProcessingScheduler {

    private static final int BATCH_SIZE = 20;

    private final ReportRepository reportRepository;
    private final ReportLeaseService leaseService;
    private final ReportProcessingService processingService;
    private final Clock clock;

    @Scheduled(
            fixedDelayString =
                    "${reporting.processing.fixed-delay-ms:5000}"
    )
    public void processPendingReports() {
        Set<UUID> candidateIds = new LinkedHashSet<>();

        try {
            candidateIds.addAll(findPendingReportIds());
            candidateIds.addAll(findExpiredProcessingReportIds());

            if (candidateIds.isEmpty()) {
                return;
            }

            log.debug(
                    "Found {} reports eligible for processing or recovery",
                    candidateIds.size()
            );

            for (UUID reportId : candidateIds) {
                processCandidate(reportId);
            }
        } catch (Exception exception) {
            log.error(
                    "Unexpected error while scanning reports for processing",
                    exception
            );
        }
    }

    private List<UUID> findPendingReportIds() {
        return reportRepository
                .findByStatusOrderByCreatedAtAsc(
                        ReportStatus.PENDING,
                        PageRequest.of(0, BATCH_SIZE)
                )
                .getContent()
                .stream()
                .map(ReportEntity::getId)
                .toList();
    }

    private List<UUID> findExpiredProcessingReportIds() {
        Instant now = Instant.now(clock);

        return reportRepository.findExpiredProcessingReportIds(
                ReportStatus.PROCESSING,
                now,
                PageRequest.of(0, BATCH_SIZE)
        );
    }

    private void processCandidate(UUID reportId) {
        try {
            leaseService.claimOrRecover(reportId)
                    .ifPresentOrElse(
                            token -> processingService
                                    .processClaimedReport(reportId, token),
                            () -> log.debug(
                                    "Report {} was claimed by another worker "
                                            + "or is no longer eligible",
                                    reportId
                            )
                    );
        } catch (Exception exception) {
            log.error(
                    "Failed to process report candidate {}",
                    reportId,
                    exception
            );
        }
    }
}