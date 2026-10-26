
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStorageCleanupService {

    private final ReportStorage reportStorage;

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
        } catch (Exception exception) {
            log.error(
                    "Storage cleanup failed for report {}. Object key: {}",
                    reportId,
                    objectKey,
                    exception
            );

            return false;
        }
    }
}
