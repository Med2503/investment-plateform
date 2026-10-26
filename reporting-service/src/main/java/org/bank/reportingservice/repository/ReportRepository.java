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

    long countByUserIdAndStatus(
            String userId,
            ReportStatus status
    );

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

    boolean existsByIdAndUserId(UUID id, String userId);

    Page<ReportEntity> findByUserIdAndStatusAndReportTypeOrderByCreatedAtDesc(
            String userId,
            ReportStatus status,
            ReportType reportType,
            Pageable pageable
    );

    @Modifying
    @Query("""
            UPDATE ReportEntity r
            SET r.status = :processingStatus,
                r.startedAt = :startedAt,
                r.updatedAt = :startedAt,
                r.version = r.version + 1
            WHERE r.id = :reportId
              AND r.status = :pendingStatus
            """)
    int claimPendingReport(
            @Param("reportId") UUID reportId,
            @Param("pendingStatus") ReportStatus pendingStatus,
            @Param("processingStatus") ReportStatus processingStatus,
            @Param("startedAt") Instant startedAt
    );
}