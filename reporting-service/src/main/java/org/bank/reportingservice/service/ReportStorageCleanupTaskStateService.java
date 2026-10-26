
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.config.ReportStorageCleanupProperties;
import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
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

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final ReportStorageCleanupProperties properties;
    private final Clock clock;

    @Transactional
    public Optional<ClaimedCleanupTask> claim(UUID taskId) {
        Instant now = Instant.now(clock);
        UUID token = UUID.randomUUID();
        Instant leaseUntil = now.plusSeconds(
                properties.leaseDurationSeconds()
        );

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
                || task.getStatus() != CleanupTaskStatus.PROCESSING
                || !token.equals(task.getProcessingToken())) {
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
                properties.maxAttempts()
        );

        return true;
    }

    private long calculateDelay(int attempt) {
        int exponent = Math.min(Math.max(attempt - 1, 0), 30);

        long initialDelay = properties.initialDelaySeconds();
        long maxDelay = properties.maxDelaySeconds();

        // Avoid overflow before applying the maximum delay.
        if (exponent >= 63
                || initialDelay > (Long.MAX_VALUE >> exponent)) {
            return maxDelay;
        }

        long delay = initialDelay * (1L << exponent);
        return Math.min(delay, maxDelay);
    }

    public record ClaimedCleanupTask(
            UUID id,
            String reportId,
            String objectKey,
            UUID token
    ) {
    }
}
