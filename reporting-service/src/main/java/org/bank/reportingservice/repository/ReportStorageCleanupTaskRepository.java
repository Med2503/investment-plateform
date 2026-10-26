
package org.bank.reportingservice.repository;

import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface ReportStorageCleanupTaskRepository
        extends JpaRepository<ReportStorageCleanupTaskEntity, UUID> {

    boolean existsByObjectKey(String objectKey);

    List<ReportStorageCleanupTaskEntity>
    findByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
            CleanupTaskStatus status,
            Instant now,
            Pageable pageable
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
}
