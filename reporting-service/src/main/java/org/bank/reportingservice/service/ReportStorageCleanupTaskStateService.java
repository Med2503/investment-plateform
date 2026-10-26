
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportStorageCleanupTaskStateService {

    private static final int MAX_ATTEMPTS = 10;
    private static final long INITIAL_DELAY_SECONDS = 30;
    private static final long MAX_DELAY_SECONDS = 21_600;

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final Clock clock;

    @Value("${reporting.storage.cleanup.lease-duration-seconds:300}")
    private long leaseDurationSeconds;

    @Transactional
    public Optional<ClaimedCleanupTask> claim(UUID taskId) {
        if (leaseDurationSeconds <= 0) {
            throw new IllegalStateException(
                    "Cleanup lease duration must be positive"
            );
        }

        Instant now = Instant.now(clock);
        UUID token = UUID.randomUUID();
        Instant leaseUntil = now.plusSeconds(leaseDurationSeconds);

        int updated = taskRepository.claimTask(
                taskId,
                CleanupTaskStatus.PENDING,
                CleanupTaskStatus.PROCESSING,
                token,
                now,
                leaseUntil
        );

        if (updated == 0) {
            return Optional.empty();
        }

        ReportStorageCleanupTaskEntity task =
                taskRepository.findByIdForUpdate(taskId).orElse(null);

        if (task == null
                || !token.equals(task.getProcessingToken())
                || task.getStatus() != CleanupTaskStatus.PROCESSING) {
            return Optional.empty();
        }

        return Optional.of(new ClaimedCleanupTask(
                task.getId(),
                task.getReportId(),
                task.getObjectKey(),
                token
        ));
    }

    @Transactional
    public boolean complete(UUID taskId, UUID token) {
        return taskRepository.deleteClaimedTask(
                taskId,
                CleanupTaskStatus.PROCESSING,
                token
        ) == 1;
    }

    @Transactional
    public boolean recordFailure(
            UUID taskId,
            UUID token,
            String errorMessage
    ) {
        ReportStorageCleanupTaskEntity task =
                taskRepository.findByIdForUpdate(taskId).orElse(null);

        if (task == null
                || task.getStatus() != CleanupTaskStatus.PROCESSING
                || !token.equals(task.getProcessingToken())) {
            return false;
        }

        int nextAttemptNumber = task.getAttempts() + 1;
        long delaySeconds = calculateDelay(nextAttemptNumber);

        task.recordFailure(
                Instant.now(clock).plus(Duration.ofSeconds(delaySeconds)),
                errorMessage,
                MAX_ATTEMPTS
        );

        return true;
    }

    private long calculateDelay(int attempt) {
        int exponent = Math.min(Math.max(attempt - 1, 0), 10);
        long delay = INITIAL_DELAY_SECONDS * (1L << exponent);
        return Math.min(delay, MAX_DELAY_SECONDS);
    }

    public record ClaimedCleanupTask(
            UUID id,
            String reportId,
            String objectKey,
            UUID token
    ) {
    }
}
