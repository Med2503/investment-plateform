
package org.bank.reportingservice.repository;

import jakarta.persistence.LockModeType;
import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportStorageCleanupTaskRepository
        extends JpaRepository<ReportStorageCleanupTaskEntity, UUID> {

    boolean existsByObjectKey(String objectKey);

    @Modifying
    @Query(value = """
            INSERT INTO report_storage_cleanup_tasks (
                id,
                report_id,
                object_key,
                status,
                attempts,
                next_attempt_at,
                last_error,
                processing_token,
                processing_lease_until,
                created_at,
                updated_at,
                version
            )
            VALUES (
                :id,
                :reportId,
                :objectKey,
                :status,
                :attempts,
                :nextAttemptAt,
                :lastError,
                NULL,
                NULL,
                :createdAt,
                :updatedAt,
                :version
            )
            ON CONFLICT (object_key) DO NOTHING
            """, nativeQuery = true)
    int insertIfAbsent(
            @Param("id") UUID id,
            @Param("reportId") String reportId,
            @Param("objectKey") String objectKey,
            @Param("status") String status,
            @Param("attempts") int attempts,
            @Param("nextAttemptAt") Instant nextAttemptAt,
            @Param("lastError") String lastError,
            @Param("createdAt") Instant createdAt,
            @Param("updatedAt") Instant updatedAt,
            @Param("version") long version
    );

    @Query("""
            SELECT t.id
            FROM ReportStorageCleanupTaskEntity t
            WHERE (
                t.status = :pending
                AND t.nextAttemptAt <= :now
            )
            OR (
                t.status = :processing
                AND (
                    t.processingLeaseUntil IS NULL
                    OR t.processingLeaseUntil <= :now
                )
            )
            ORDER BY t.nextAttemptAt ASC, t.id ASC
            """)
    List<UUID> findEligibleTaskIds(
            @Param("pending") CleanupTaskStatus pending,
            @Param("processing") CleanupTaskStatus processing,
            @Param("now") Instant now,
            Pageable pageable
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
            UPDATE ReportStorageCleanupTaskEntity t
            SET t.status = :processing,
                t.processingToken = :token,
                t.processingLeaseUntil = :leaseUntil,
                t.updatedAt = :now,
                t.version = t.version + 1
            WHERE t.id = :taskId
              AND (
                (
                    t.status = :pending
                    AND t.nextAttemptAt <= :now
                )
                OR
                (
                    t.status = :processing
                    AND (
                        t.processingLeaseUntil IS NULL
                        OR t.processingLeaseUntil <= :now
                    )
                )
              )
            """)
    int claimTask(
            @Param("taskId") UUID taskId,
            @Param("pending") CleanupTaskStatus pending,
            @Param("processing") CleanupTaskStatus processing,
            @Param("token") UUID token,
            @Param("now") Instant now,
            @Param("leaseUntil") Instant leaseUntil
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT t
            FROM ReportStorageCleanupTaskEntity t
            WHERE t.id = :taskId
            """)
    Optional<ReportStorageCleanupTaskEntity> findByIdForUpdate(
            @Param("taskId") UUID taskId
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query("""
            DELETE FROM ReportStorageCleanupTaskEntity t
            WHERE t.id = :taskId
              AND t.status = :processing
              AND t.processingToken = :token
              AND t.processingLeaseUntil > :now
            """)
    int deleteClaimedTask(
            @Param("taskId") UUID taskId,
            @Param("processing") CleanupTaskStatus processing,
            @Param("token") UUID token,
            @Param("now") Instant now
    );
}
