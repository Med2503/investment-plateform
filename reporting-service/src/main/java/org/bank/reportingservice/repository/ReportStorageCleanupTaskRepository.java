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
            ORDER BY t.nextAttemptAt ASC
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
              AND t.processingLeaseUntil > CURRENT_TIMESTAMP
            """)
    int deleteClaimedTask(
            @Param("taskId") UUID taskId,
            @Param("processing") CleanupTaskStatus processing,
            @Param("token") UUID token
    );
}
