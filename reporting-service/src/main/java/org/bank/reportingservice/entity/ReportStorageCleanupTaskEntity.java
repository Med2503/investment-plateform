
package org.bank.reportingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "report_storage_cleanup_tasks",
        indexes = {
                @Index(
                        name = "idx_cleanup_status_next_attempt",
                        columnList = "status,next_attempt_at"
                )
        }
)
public class ReportStorageCleanupTaskEntity {

    @Id
    private UUID id;

    @Column(name = "report_id", nullable = false, length = 36)
    private String reportId;

    @Column(
            name = "object_key",
            nullable = false,
            unique = true,
            length = 1024
    )
    private String objectKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CleanupTaskStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "last_error", length = 2000)
    private String lastError;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    protected ReportStorageCleanupTaskEntity() {
    }

    public ReportStorageCleanupTaskEntity(
            String reportId,
            String objectKey,
            Instant nextAttemptAt,
            String errorMessage
    ) {
        this.id = UUID.randomUUID();
        this.reportId = reportId;
        this.objectKey = objectKey;
        this.status = CleanupTaskStatus.PENDING;
        this.attempts = 0;
        this.nextAttemptAt = nextAttemptAt;
        this.lastError = sanitize(errorMessage);
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

    public void recordFailure(
            Instant nextAttempt,
            String errorMessage,
            int maxAttempts
    ) {
        attempts++;
        lastError = sanitize(errorMessage);

        if (attempts >= maxAttempts) {
            status = CleanupTaskStatus.DEAD;
        } else {
            nextAttemptAt = nextAttempt;
        }
    }

    private String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return "Storage cleanup failed";
        }

        String sanitized = message
                .replace('\r', ' ')
                .replace('\n', ' ')
                .replace('\t', ' ');

        return sanitized.length() > 2000
                ? sanitized.substring(0, 2000)
                : sanitized;
    }

    public UUID getId() {
        return id;
    }

    public String getReportId() {
        return reportId;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public CleanupTaskStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public String getLastError() {
        return lastError;
    }
}
