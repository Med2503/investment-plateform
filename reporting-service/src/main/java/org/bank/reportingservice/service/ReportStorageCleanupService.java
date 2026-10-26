
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
                    "Failed to delete report storage object: reportId={}",
                    reportId,
                    storageException
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
                    "Cleanup task already exists for object key"
            );
        } catch (Exception exception) {
            log.error(
                    "Unable to persist cleanup task: reportId={}",
                    reportId,
                    exception
            );
        }
    }
}
