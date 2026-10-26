
package org.bank.reportingservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.config.ReportStorageCleanupProperties;
import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.bank.reportingservice.service.ReportStorageCleanupRetryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportStorageCleanupRetryScheduler {

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final ReportStorageCleanupRetryService retryService;
    private final ReportStorageCleanupProperties properties;
    private final Clock clock;

    @Scheduled(
            fixedDelayString =
                    "${reporting.storage.cleanup.fixed-delay-ms:30000}"
    )
    public void retryFailedCleanups() {
        List<UUID> taskIds;

        try {
            int batchSize = properties.batchSize();

            if (batchSize <= 0) {
                log.error(
                        "Invalid storage cleanup batch size: {}",
                        batchSize
                );
                return;
            }

            Instant now = Instant.now(clock);

            taskIds = taskRepository.findEligibleTaskIds(
                    CleanupTaskStatus.PENDING,
                    CleanupTaskStatus.PROCESSING,
                    now,
                    PageRequest.of(0, batchSize)
            );
        } catch (Exception exception) {
            log.error(
                    "Could not retrieve eligible storage cleanup tasks",
                    exception
            );
            return;
        }

        if (taskIds.isEmpty()) {
            log.debug("No eligible storage cleanup tasks found");
            return;
        }

        log.info(
                "Processing eligible storage cleanup tasks: count={}",
                taskIds.size()
        );

        int processed = 0;
        int unexpectedErrors = 0;

        for (UUID taskId : taskIds) {
            try {
                retryService.processTask(taskId);
                processed++;
            } catch (Exception exception) {
                unexpectedErrors++;

                log.error(
                        "Unexpected error processing cleanup task: taskId={}",
                        taskId,
                        exception
                );
            }
        }

        log.info(
                "Storage cleanup batch finished: selected={}, "
                        + "processed={}, unexpectedErrors={}",
                taskIds.size(),
                processed,
                unexpectedErrors
        );
    }
}
