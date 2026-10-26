
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
        Instant now = Instant.now(clock);

        List<UUID> taskIds = taskRepository.findEligibleTaskIds(
                CleanupTaskStatus.PENDING,
                CleanupTaskStatus.PROCESSING,
                now,
                PageRequest.of(0, properties.batchSize())
        );

        if (taskIds.isEmpty()) {
            return;
        }

        log.info(
                "Processing eligible storage cleanup tasks: count={}",
                taskIds.size()
        );

        for (UUID taskId : taskIds) {
            try {
                retryService.processTask(taskId);
            } catch (Exception exception) {
                log.error(
                        "Unexpected error processing cleanup task: taskId={}",
                        taskId,
                        exception
                );
            }
        }
    }
}
