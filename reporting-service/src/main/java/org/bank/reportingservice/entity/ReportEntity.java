package org.bank.reportingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import org.bank.reportingservice.model.ReportFormat;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.model.ReportType;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "reports",
        indexes = {
                @Index(
                        name = "idx_reports_user_created",
                        columnList = "user_id, created_at"
                ),
                @Index(
                        name = "idx_reports_status_created",
                        columnList = "status, created_at"
                )
        }
)
public class ReportEntity {

    private static final int MAX_ERROR_LENGTH = 2000;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 40)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_format", nullable = false, length = 10)
    private ReportFormat format;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status;

    @Column(name = "period_start", nullable = false)
    private Instant periodStart;

    @Column(name = "period_end", nullable = false)
    private Instant periodEnd;

    @Column(name = "file_location", length = 1000)
    private String fileLocation;

    @Column(name = "error_message", length = MAX_ERROR_LENGTH)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private long version;

    @Column(name = "processing_lease_until")
    private Instant processingLeaseUntil;

    @Column(name = "processing_token")
    private UUID processingToken;

    protected ReportEntity() {
        // Required by JPA.
    }

    public ReportEntity(
            String userId,
            ReportType reportType,
            ReportFormat format,
            Instant periodStart,
            Instant periodEnd
    ) {
        this.userId = userId;
        this.reportType = reportType;
        this.format = format;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.status = ReportStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public void markProcessing(Instant startedAt) {
        if (status != ReportStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending reports can start processing"
            );
        }

        this.status = ReportStatus.PROCESSING;
        this.startedAt = startedAt;
        this.completedAt = null;
        this.fileLocation = null;
        this.errorMessage = null;
    }

    public void markCompleted(String fileLocation, Instant completedAt) {
        if (status != ReportStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing reports can be completed"
            );
        }

        if (fileLocation == null || fileLocation.isBlank()) {
            throw new IllegalArgumentException(
                    "File location must not be blank"
            );
        }

        this.fileLocation = fileLocation;
        this.completedAt = completedAt;
        this.status = ReportStatus.COMPLETED;
        this.errorMessage = null;
        clearProcessingLease();
    }

    public void markFailed(String errorMessage, Instant completedAt) {
        if (status != ReportStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing reports can fail"
            );
        }

        if (errorMessage == null || errorMessage.isBlank()) {
            throw new IllegalArgumentException(
                    "Error message must not be blank"
            );
        }

        this.errorMessage = errorMessage.length() > MAX_ERROR_LENGTH
                ? errorMessage.substring(0, MAX_ERROR_LENGTH)
                : errorMessage;

        this.completedAt = completedAt;
        this.status = ReportStatus.FAILED;
        clearProcessingLease();
    }

    public void renewProcessingLease(Instant leaseUntil) {
        if (status != ReportStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Only processing reports can renew their lease"
            );
        }

        if (leaseUntil == null) {
            throw new IllegalArgumentException(
                    "Lease expiration must not be null"
            );
        }

        this.processingLeaseUntil = leaseUntil;
    }

    public void clearProcessingLease() {
        this.processingLeaseUntil = null;
        this.processingToken = null;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public Instant getPeriodStart() {
        return periodStart;
    }

    public Instant getPeriodEnd() {
        return periodEnd;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public long getVersion() {
        return version;
    }

    public Instant getProcessingLeaseUntil() {
        return processingLeaseUntil;
    }

    public UUID getProcessingToken() {
        return processingToken;
    }
}
