package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStorageCleanupService {

    private final ReportStorage reportStorage;
    private final ReportStorageCleanupTaskService cleanupTaskService;

    public boolean deleteIfPresent(
            String objectKey,
            String reportId
    ) {
        if (objectKey == null || objectKey.isBlank()) {
            return true;
        }

        try {
            reportStorage.delete(objectKey);

            log.info(
                    "Report storage object deleted: reportId={}",
                    reportId
            );

            return true;
        } catch (Exception storageException) {
            log.error(
                    "Report storage deletion failed: reportId={}, exceptionType={}",
                    reportId,
                    storageException.getClass().getSimpleName()
            );

            enqueueForRetry(
                    objectKey,
                    reportId,
                    storageException.getClass().getSimpleName()
            );

            return false;
        }
    }

    private void enqueueForRetry(
            String objectKey,
            String reportId,
            String errorMessage
    ) {
        try {
            cleanupTaskService.enqueue(
                    objectKey,
                    reportId,
                    errorMessage
            );

            log.info(
                    "Report storage cleanup scheduled for retry: reportId={}",
                    reportId
            );
        } catch (DataIntegrityViolationException exception) {
            log.info(
                    "Cleanup task already exists or violates a database constraint: reportId={}",
                    reportId
            );
        } catch (Exception exception) {
            log.error(
                    "Unable to schedule report storage cleanup: reportId={}, exceptionType={}",
                    reportId,
                    exception.getClass().getSimpleName()
            );
        }
    }
}
