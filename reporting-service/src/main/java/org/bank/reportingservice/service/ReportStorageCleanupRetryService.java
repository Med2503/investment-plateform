
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStorageCleanupRetryService {

    private static final int MAX_ATTEMPTS = 10;
    private static final long INITIAL_DELAY_SECONDS = 30;
    private static final long MAX_DELAY_SECONDS = 21_600;

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final ReportStorage reportStorage;

    @Transactional
    public void processTask(UUID taskId) {
        ReportStorageCleanupTaskEntity task =
                taskRepository.findByIdForUpdate(taskId).orElse(null);

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
            int nextAttemptNumber = task.getAttempts() + 1;
            long delaySeconds = calculateDelay(nextAttemptNumber);

            task.recordFailure(
                    Instant.now().plus(Duration.ofSeconds(delaySeconds)),
                    exception.getMessage(),
                    MAX_ATTEMPTS
            );

            if (task.getStatus() == CleanupTaskStatus.DEAD) {
                log.error(
                        "Cleanup task {} reached maximum attempts. "
                                + "Manual intervention required for key {}",
                        task.getId(),
                        task.getObjectKey(),
                        exception
                );
            } else {
                log.warn(
                        "Cleanup failed for report {}. Attempt {}/{}. "
                                + "Retry in {} seconds.",
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
