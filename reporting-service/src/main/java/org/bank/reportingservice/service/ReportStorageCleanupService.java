
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStorageCleanupService {

    private final ReportStorage reportStorage;
    private final ReportStorageCleanupTaskRepository cleanupTaskRepository;

    public boolean deleteIfPresent(String objectKey, String reportId) {
        if (objectKey == null || objectKey.isBlank()) {
            return true;
        }

        try {
            reportStorage.delete(objectKey);

            log.info(
                    "Storage cleanup succeeded for report {}",
                    reportId
            );

            return true;
        } catch (Exception storageException) {
            log.error(
                    "Immediate storage cleanup failed for report {}",
                    reportId,
                    storageException
            );

            try {
                enqueueCleanup(
                        objectKey,
                        reportId,
                        storageException.getMessage()
                );
            } catch (Exception persistenceException) {
                log.error(
                        "Could not persist cleanup task for report {}. "
                                + "Object key: {}",
                        reportId,
                        objectKey,
                        persistenceException
                );
            }

            return false;
        }
    }

    @Transactional
    public void enqueueCleanup(
            String objectKey,
            String reportId,
            String errorMessage
    ) {
        if (cleanupTaskRepository.existsByObjectKey(objectKey)) {
            log.info(
                    "Cleanup task already exists for object key {}",
                    objectKey
            );
            return;
        }

        ReportStorageCleanupTaskEntity task =
                new ReportStorageCleanupTaskEntity(
                        reportId,
                        objectKey,
                        Instant.now().plusSeconds(30),
                        errorMessage
                );

        try {
            cleanupTaskRepository.save(task);
        } catch (DataIntegrityViolationException exception) {
            // Another application instance may have inserted the same key.
            log.info(
                    "Cleanup task already persisted for object key {}",
                    objectKey
            );
        }
    }
}
