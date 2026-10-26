
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStorageCleanupRetryService {

    private final ReportStorage reportStorage;
    private final ReportStorageCleanupTaskStateService stateService;

    public void processTask(UUID taskId) {
        Optional<ReportStorageCleanupTaskStateService.ClaimedCleanupTask>
                claim = stateService.claim(taskId);

        if (claim.isEmpty()) {
            return;
        }

        ReportStorageCleanupTaskStateService.ClaimedCleanupTask task =
                claim.get();

        try {

            reportStorage.delete(task.objectKey());

            boolean completed = stateService.complete(
                    task.id(),
                    task.token()
            );

            if (completed) {
                log.info(
                        "Cleanup completed: reportId={}",
                        task.reportId()
                );
            } else {
                log.warn(
                        "Cleanup task was no longer owned by this worker: taskId={}",
                        task.id()
                );
            }
        } catch (Exception exception) {
            log.error(
                    "Cleanup failed: reportId={}",
                    task.reportId(),
                    exception
            );

            try {
                boolean recorded = stateService.recordFailure(
                        task.id(),
                        task.token(),
                        exception.getMessage()
                );

                if (!recorded) {
                    log.warn(
                            "Could not record cleanup failure because the lease "
                                    + "is no longer owned: taskId={}",
                            task.id()
                    );
                }
            } catch (Exception persistenceException) {

                log.error(
                        "Unable to persist cleanup failure; lease recovery "
                                + "will be used: taskId={}",
                        task.id(),
                        persistenceException
                );
            }
        }
    }
}
