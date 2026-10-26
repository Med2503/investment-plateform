
package org.bank.reportingservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.bank.reportingservice.service.ReportStorage;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportStorageCleanupRetryScheduler {

    private static final int BATCH_SIZE = 50;
    private static final int MAX_ATTEMPTS = 10;
    private static final long INITIAL_DELAY_SECONDS = 30;
    private static final long MAX_DELAY_SECONDS = 21_600;

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final ReportStorage reportStorage;

    @Scheduled(
            fixedDelayString =
                    "${reporting.storage.cleanup.fixed-delay-ms:30000}"
    )
    public void retryFailedCleanups() {
        List<ReportStorageCleanupTaskEntity> tasks =
                taskRepository
                        .findByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
                                CleanupTaskStatus.PENDING,
                                Instant.now(),
                                PageRequest.of(0, BATCH_SIZE)
                        );

        for (ReportStorageCleanupTaskEntity task : tasks) {
            try {
                processTask(task.getId());
            } catch (Exception exception) {
                log.error(
                        "Unexpected error processing cleanup task {}",
                        task.getId(),
                        exception
                );
            }
        }
    }

    @Transactional
    public void processTask(java.util.UUID taskId) {
        ReportStorageCleanupTaskEntity task =
                taskRepository.findById(taskId).orElse(null);

        if (task == null
                || task.getStatus() != CleanupTaskStatus.PENDING
                || task.getNextAttemptAt().isAfter(Instant.now())) {
            return;
        }

        try {
            reportStorage.delete(task.getObjectKey());

            taskRepository.delete(task);

            log.info(
                    "Deferred cleanup succeeded for report {}",
                    task.getReportId()
            );
        } catch (Exception exception) {
            Instant now = Instant.now();
            int nextAttemptNumber = task.getAttempts() + 1;
            long delaySeconds = calculateDelay(nextAttemptNumber);

            task.recordFailure(
                    now.plus(Duration.ofSeconds(delaySeconds)),
                    exception.getMessage(),
                    MAX_ATTEMPTS
            );

            if (task.getStatus() == CleanupTaskStatus.DEAD) {
                log.error(
                        "Cleanup task {} reached maximum attempts. "
                                + "Manual intervention may be required. "
                                + "Object key: {}",
                        task.getId(),
                        task.getObjectKey(),
                        exception
                );
            } else {
                log.warn(
                        "Cleanup retry failed for report {}. "
                                + "Attempt {} of {}. Next retry in {} seconds.",
                        task.getReportId(),
                        task.getAttempts(),
                        MAX_ATTEMPTS,
                        delaySeconds,
                        exception
                );
            }
        }
    }

    private long calculateDelay(int attempt) {
        int exponent = Math.min(Math.max(attempt - 1, 0), 10);

        long delay = INITIAL_DELAY_SECONDS * (1L << exponent);

        return Math.min(delay, MAX_DELAY_SECONDS);
    }
}
