package org.bank.reportingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "report_storage_cleanup_tasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportStorageCleanupTaskEntity {

    private static final int MAX_ERROR_LENGTH = 1000;
    private static final int MAX_OBJECT_KEY_LENGTH = 1024;
    private static final int MAX_REPORT_ID_LENGTH = 36;

    @Id
    private UUID id;

    @Column(name = "report_id", length = MAX_REPORT_ID_LENGTH)
    private String reportId;

    @Column(
            name = "object_key",
            nullable = false,
            unique = true,
            length = MAX_OBJECT_KEY_LENGTH
    )
    private String objectKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CleanupTaskStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "last_error", length = MAX_ERROR_LENGTH)
    private String lastError;

    @Column(name = "processing_token")
    private UUID processingToken;

    @Column(name = "processing_lease_until")
    private Instant processingLeaseUntil;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    public ReportStorageCleanupTaskEntity(
            String reportId,
            String objectKey,
            Instant nextAttemptAt,
            String errorMessage
    ) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Object key must not be blank"
            );
        }

        if (objectKey.length() > MAX_OBJECT_KEY_LENGTH) {
            throw new IllegalArgumentException(
                    "Object key exceeds the maximum length"
            );
        }

        if (reportId != null
                && reportId.length() > MAX_REPORT_ID_LENGTH) {
            throw new IllegalArgumentException(
                    "Report ID exceeds the maximum length"
            );
        }

        this.id = UUID.randomUUID();
        this.reportId = reportId;
        this.objectKey = objectKey;
        this.status = CleanupTaskStatus.PENDING;
        this.attempts = 0;
        this.nextAttemptAt = java.util.Objects.requireNonNull(
                nextAttemptAt,
                "Next attempt time must not be null"
        );
        this.lastError = sanitize(errorMessage);
    }

    public void recordFailure(
            Instant nextAttempt,
            String errorMessage,
            int maxAttempts
    ) {
        if (this.status != CleanupTaskStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing cleanup tasks can record a failure"
            );
        }

        if (maxAttempts < 1) {
            throw new IllegalArgumentException(
                    "Maximum attempts must be positive"
            );
        }

        if (this.attempts < 0 || this.attempts == Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "Invalid cleanup attempt count"
            );
        }

        this.attempts++;
        this.lastError = sanitize(errorMessage);
        this.processingToken = null;
        this.processingLeaseUntil = null;

        if (this.attempts >= maxAttempts) {
            this.status = CleanupTaskStatus.DEAD;
        } else {
            this.nextAttemptAt = java.util.Objects.requireNonNull(
                    nextAttempt,
                    "Next attempt time must not be null"
            );
            this.status = CleanupTaskStatus.PENDING;
        }
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    private static String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return "Storage cleanup failed";
        }

        String normalized = message.replaceAll("[\\r\\n\\t]", " ");

        return normalized.length() <= MAX_ERROR_LENGTH
                ? normalized
                : normalized.substring(0, MAX_ERROR_LENGTH);
    }
}
