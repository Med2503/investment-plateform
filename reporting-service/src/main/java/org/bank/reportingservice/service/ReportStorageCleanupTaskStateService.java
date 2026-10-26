
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.config.ReportStorageCleanupProperties;
import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportStorageCleanupTaskStateService {

    private static final int MAX_ERROR_LENGTH = 1000;

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final ReportStorageCleanupProperties properties;
    private final Clock clock;

    @Transactional
    public Optional<ClaimedCleanupTask> claim(UUID taskId) {
        Objects.requireNonNull(taskId, "Cleanup task ID must not be null");
        validateConfiguration();

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

        if (updated != 1) {
            return Optional.empty();
        }

        ReportStorageCleanupTaskEntity task =
                taskRepository.findByIdForUpdate(taskId).orElse(null);

        if (task == null
                || task.getStatus() != CleanupTaskStatus.PROCESSING
                || !token.equals(task.getProcessingToken())
                || task.getProcessingLeaseUntil() == null
                || !task.getProcessingLeaseUntil().isAfter(
                Instant.now(clock)
        )) {
            return Optional.empty();
        }

        return Optional.of(
                new ClaimedCleanupTask(
                        task.getId(),
                        task.getReportId(),
                        task.getObjectKey(),
                        token
                )
        );
    }

    @Transactional
    public boolean complete(UUID taskId, UUID token) {
        validateIdentifiers(taskId, token);

        Instant now = Instant.now(clock);

        ReportStorageCleanupTaskEntity task =
                taskRepository.findByIdForUpdate(taskId).orElse(null);

        if (!ownsActiveLease(task, token, now)) {
            return false;
        }

        return taskRepository.deleteClaimedTask(
                taskId,
                CleanupTaskStatus.PROCESSING,
                token,
                now
        ) == 1;
    }

    @Transactional
    public boolean recordFailure(
            UUID taskId,
            UUID token,
            String errorMessage
    ) {
        validateIdentifiers(taskId, token);
        validateConfiguration();

        Instant now = Instant.now(clock);

        ReportStorageCleanupTaskEntity task =
                taskRepository.findByIdForUpdate(taskId).orElse(null);

        if (!ownsActiveLease(task, token, now)) {
            return false;
        }
        if (task.getAttempts() < 0 || task.getAttempts() == Integer.MAX_VALUE) {
            throw new IllegalStateException("Invalid cleanup attempt count");
        }

        int nextAttemptNumber = task.getAttempts() + 1;
        long delaySeconds = calculateDelay(nextAttemptNumber);

        Instant nextAttemptAt = now.plusSeconds(delaySeconds);

        task.recordFailure(
                nextAttemptAt,
                sanitizeErrorMessage(errorMessage),
                properties.maxAttempts()
        );

        return true;
    }

    private boolean ownsActiveLease(
            ReportStorageCleanupTaskEntity task,
            UUID token,
            Instant now
    ) {
        return task != null
                && task.getStatus() == CleanupTaskStatus.PROCESSING
                && token.equals(task.getProcessingToken())
                && task.getProcessingLeaseUntil() != null
                && task.getProcessingLeaseUntil().isAfter(now);
    }

    private long calculateDelay(int attempt) {
        long initialDelay = properties.initialDelaySeconds();
        long maxDelay = properties.maxDelaySeconds();

        if (attempt < 1 || initialDelay <= 0 || maxDelay <= 0) {
            throw new IllegalStateException(
                    "Invalid cleanup retry backoff configuration"
            );
        }

        if (initialDelay >= maxDelay) {
            return maxDelay;
        }

        int exponent = Math.min(attempt - 1, 30);
        long multiplier = 1L << exponent;

        if (initialDelay > maxDelay / multiplier) {
            return maxDelay;
        }

        return Math.min(initialDelay * multiplier, maxDelay);
    }

    private void validateConfiguration() {
        if (properties.leaseDurationSeconds() <= 0) {
            throw new IllegalStateException(
                    "Cleanup lease duration must be positive"
            );
        }

        if (properties.maxAttempts() <= 0) {
            throw new IllegalStateException(
                    "Cleanup max attempts must be positive"
            );
        }

        if (properties.initialDelaySeconds() <= 0
                || properties.maxDelaySeconds() <= 0) {
            throw new IllegalStateException(
                    "Cleanup retry delays must be positive"
            );
        }
    }

    private void validateIdentifiers(UUID taskId, UUID token) {
        Objects.requireNonNull(taskId, "Cleanup task ID must not be null");
        Objects.requireNonNull(token, "Cleanup lease token must not be null");
    }

    private String sanitizeErrorMessage(String errorMessage) {
        if (errorMessage == null || errorMessage.isBlank()) {
            return "Storage cleanup failed";
        }

        String normalized = errorMessage.replaceAll("[\\r\\n\\t]", " ");

        return normalized.substring(
                0,
                Math.min(normalized.length(), MAX_ERROR_LENGTH)
        );
    }

    public record ClaimedCleanupTask(
            UUID id,
            String reportId,
            String objectKey,
            UUID token
    ) {
    }
}
