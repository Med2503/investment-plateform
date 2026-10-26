
package org.bank.reportingservice.repository;

import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReportStorageCleanupTaskRepository
        extends JpaRepository<ReportStorageCleanupTaskEntity, UUID> {

    boolean existsByObjectKey(String objectKey);

    List<ReportStorageCleanupTaskEntity>
    findByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
            CleanupTaskStatus status,
            Instant now,
            Pageable pageable
    );
}
