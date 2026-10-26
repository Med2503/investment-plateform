package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.service.ReportStorage;
import org.bank.reportingservice.service.ReportStorageCleanupTaskService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStorageCleanupService {

    private final ReportStorage reportStorage;
    private final ReportStorageCleanupTaskService cleanupTaskService;

    /**
     * @return true if deletion succeeded or a retry was scheduled;
     *         false if deletion failed and retry scheduling could not
     *         be confirmed.
     */
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

            return enqueueForRetry(
                    objectKey,
                    reportId,
                    storageException.getClass().getSimpleName()
            );
        }
    }

    private boolean enqueueForRetry(
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
                    "Report storage cleanup retry scheduled: reportId={}",
                    reportId
            );

            return true;
        } catch (DataIntegrityViolationException exception) {
            log.error(
                    "Could not confirm cleanup task persistence: reportId={}, reason=constraint_violation",
                    reportId
            );

            return false;
        } catch (Exception exception) {
            log.error(
                    "Report storage cleanup retry scheduling failed: reportId={}, exceptionType={}",
                    reportId,
                    exception.getClass().getSimpleName()
            );

            return false;
        }
    }
}
