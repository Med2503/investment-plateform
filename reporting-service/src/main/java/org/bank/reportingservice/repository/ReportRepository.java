package org.bank.reportingservice.repository;

import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.model.ReportType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {

    Optional<ReportEntity> findByIdAndUserId(UUID id, String userId);

    Page<ReportEntity> findByUserIdOrderByCreatedAtDesc(
            String userId,
            Pageable pageable
    );

    Page<ReportEntity> findByUserIdAndStatusOrderByCreatedAtDesc(
            String userId,
            ReportStatus status,
            Pageable pageable
    );

    Page<ReportEntity> findByUserIdAndReportTypeOrderByCreatedAtDesc(
            String userId,
            ReportType reportType,
            Pageable pageable
    );

    Page<ReportEntity> findByStatusOrderByCreatedAtAsc(
            ReportStatus status,
            Pageable pageable
    );

    Page<ReportEntity> findByUserIdAndStatusAndReportTypeOrderByCreatedAtDesc(
            String userId,
            ReportStatus status,
            ReportType reportType,
            Pageable pageable
    );

    long countByUserIdAndStatus(String userId, ReportStatus status);

    boolean existsByIdAndUserId(UUID id, String userId);

    @Query("""
            SELECT COUNT(r)
            FROM ReportEntity r
            WHERE r.userId = :userId
              AND r.createdAt >= :since
            """)
    long countRecentReports(
            @Param("userId") String userId,
            @Param("since") Instant since
    );

    /**
     * Claims a pending report or reclaims one whose processing lease expired.
     *
     * A NULL lease is also considered expired to recover legacy or
     * partially migrated PROCESSING records.
     */
    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
            UPDATE ReportEntity r
            SET r.status = :processingStatus,
                r.startedAt = :startedAt,
                r.updatedAt = :startedAt,
                r.processingLeaseUntil = :leaseUntil,
                r.processingToken = :token,
                r.version = r.version + 1
            WHERE r.id = :reportId
              AND (
                    r.status = :pendingStatus
                    OR (
                        r.status = :processingStatus
                        AND (
                            r.processingLeaseUntil IS NULL
                            OR r.processingLeaseUntil < :startedAt
                        )
                    )
              )
            """)
    int claimOrRecoverReport(
            @Param("reportId") UUID reportId,
            @Param("pendingStatus") ReportStatus pendingStatus,
            @Param("processingStatus") ReportStatus processingStatus,
            @Param("startedAt") Instant startedAt,
            @Param("leaseUntil") Instant leaseUntil,
            @Param("token") UUID token
    );

    /**
     * Renews a lease only while the current worker still owns it.
     */
    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
            UPDATE ReportEntity r
            SET r.processingLeaseUntil = :leaseUntil,
                r.updatedAt = :now,
                r.version = r.version + 1
            WHERE r.id = :reportId
              AND r.status = :processingStatus
              AND r.processingToken = :token
              AND r.processingLeaseUntil >= :now
            """)
    int renewLease(
            @Param("reportId") UUID reportId,
            @Param("processingStatus") ReportStatus processingStatus,
            @Param("token") UUID token,
            @Param("now") Instant now,
            @Param("leaseUntil") Instant leaseUntil
    );

    /**
     * Finds PROCESSING reports with missing or expired leases.
     */
    @Query("""
            SELECT r.id
            FROM ReportEntity r
            WHERE r.status = :processingStatus
              AND (
                    r.processingLeaseUntil IS NULL
                    OR r.processingLeaseUntil < :now
              )
            ORDER BY r.createdAt ASC
            """)
    List<UUID> findExpiredProcessingReportIds(
            @Param("processingStatus") ReportStatus processingStatus,
            @Param("now") Instant now,
            Pageable pageable
    );

    /**
     * Finalizes a report only if the caller still owns its valid lease.
     */
    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
            UPDATE ReportEntity r
            SET r.status = :completedStatus,
                r.fileLocation = :fileLocation,
                r.completedAt = :completedAt,
                r.updatedAt = :completedAt,
                r.processingLeaseUntil = null,
                r.processingToken = null,
                r.version = r.version + 1
            WHERE r.id = :reportId
              AND r.status = :processingStatus
              AND r.processingToken = :token
              AND r.processingLeaseUntil >= :completedAt
            """)
    int completeClaimedReport(
            @Param("reportId") UUID reportId,
            @Param("processingStatus") ReportStatus processingStatus,
            @Param("completedStatus") ReportStatus completedStatus,
            @Param("token") UUID token,
            @Param("fileLocation") String fileLocation,
            @Param("completedAt") Instant completedAt
    );

    /**
     * Marks a report as failed only if the caller still owns its valid lease.
     */
    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
            UPDATE ReportEntity r
            SET r.status = :failedStatus,
                r.errorMessage = :errorMessage,
                r.completedAt = :failedAt,
                r.updatedAt = :failedAt,
                r.processingLeaseUntil = null,
                r.processingToken = null,
                r.version = r.version + 1
            WHERE r.id = :reportId
              AND r.status = :processingStatus
              AND r.processingToken = :token
              AND r.processingLeaseUntil >= :failedAt
            """)
    int failClaimedReport(
            @Param("reportId") UUID reportId,
            @Param("processingStatus") ReportStatus processingStatus,
            @Param("failedStatus") ReportStatus failedStatus,
            @Param("token") UUID token,
            @Param("errorMessage") String errorMessage,
            @Param("failedAt") Instant failedAt
    );
}