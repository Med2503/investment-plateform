
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

    @Id
    private UUID id;

    @Column(name = "report_id", length = 36)
    private String reportId;

    @Column(name = "object_key", nullable = false, unique = true, length = 1024)
    private String objectKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CleanupTaskStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "last_error", length = 1000)
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
        this.id = UUID.randomUUID();
        this.reportId = reportId;
        this.objectKey = objectKey;
        this.status = CleanupTaskStatus.PENDING;
        this.attempts = 0;
        this.nextAttemptAt = nextAttemptAt;
        this.lastError = sanitize(errorMessage);
    }

    public void recordFailure(
            Instant nextAttempt,
            String errorMessage,
            int maxAttempts
    ) {
        this.attempts++;
        this.lastError = sanitize(errorMessage);
        this.processingToken = null;
        this.processingLeaseUntil = null;

        if (this.attempts >= maxAttempts) {
            this.status = CleanupTaskStatus.DEAD;
        } else {
            this.status = CleanupTaskStatus.PENDING;
            this.nextAttemptAt = nextAttempt;
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
        return normalized.length() <= 1000
                ? normalized
                : normalized.substring(0, 1000);
    }
}
