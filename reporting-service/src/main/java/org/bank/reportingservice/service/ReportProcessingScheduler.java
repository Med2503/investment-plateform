
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportProcessingScheduler {

    private static final int BATCH_SIZE = 20;
    private static final int CATEGORY_BATCH_SIZE = BATCH_SIZE / 2;

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

        candidateIds.addAll(findPendingReportIds());
        candidateIds.addAll(findExpiredProcessingReportIds());

        if (candidateIds.isEmpty()) {
            log.debug("No report candidates found for processing");
            return;
        }

        log.debug(
                "Found {} report candidates for processing or recovery",
                candidateIds.size()
        );

        for (UUID reportId : candidateIds) {
            processCandidate(reportId);
        }
    }

    private List<UUID> findPendingReportIds() {
        try {
            return reportRepository.findByStatusOrderByCreatedAtAsc(
                            ReportStatus.PENDING,
                            PageRequest.of(0, CATEGORY_BATCH_SIZE)
                    )
                    .getContent()
                    .stream()
                    .map(ReportEntity::getId)
                    .toList();
        } catch (Exception exception) {
            log.error(
                    "Failed to retrieve pending report candidates",
                    exception
            );
            return List.of();
        }
    }

    private List<UUID> findExpiredProcessingReportIds() {
        try {
            Instant now = Instant.now(clock);

            return reportRepository.findExpiredProcessingReportIds(
                    ReportStatus.PROCESSING,
                    now,
                    PageRequest.of(0, CATEGORY_BATCH_SIZE)
            );
        } catch (Exception exception) {
            log.error(
                    "Failed to retrieve expired processing report candidates",
                    exception
            );
            return List.of();
        }
    }

    private void processCandidate(UUID reportId) {
        try {
            leaseService.claimOrRecover(reportId)
                    .ifPresentOrElse(
                            token -> processingService.processClaimedReport(
                                    reportId,
                                    token
                            ),
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
