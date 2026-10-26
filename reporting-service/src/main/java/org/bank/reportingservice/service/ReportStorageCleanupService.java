
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
                    storageException.getMessage()
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
        } catch (DataIntegrityViolationException exception) {
            log.info(
                    "Cleanup task already exists or violates a database constraint: "
                            + "reportId={}",
                    reportId
            );
        } catch (Exception exception) {
            log.error(
                    "Cleanup task persistence failed: reportId={}, exceptionType={}",
                    reportId,
                    exception.getClass().getSimpleName()
            );
        }
    }
}
