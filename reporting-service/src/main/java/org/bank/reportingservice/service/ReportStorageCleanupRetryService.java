package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.exception.ReportStorageException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportStorageCleanupRetryService {

    private final ReportStorage reportStorage;
    private final ReportStorageCleanupTaskStateService stateService;

    public void processTask(UUID taskId) {
        Objects.requireNonNull(
                taskId,
                "Cleanup task ID must not be null"
        );

        Optional<ReportStorageCleanupTaskStateService.ClaimedCleanupTask>
                claim = stateService.claim(taskId);

        if (claim.isEmpty()) {
            log.debug(
                    "Cleanup task is not claimable: taskId={}",
                    taskId
            );
            return;
        }

        ReportStorageCleanupTaskStateService.ClaimedCleanupTask task =
                claim.get();

        try {
            reportStorage.delete(task.objectKey());
        } catch (ReportStorageException storageException) {
            log.error(
                    "Cleanup deletion failed: reportId={}, exceptionType={}",
                    task.reportId(),
                    storageException.getClass().getSimpleName(),
                    storageException
            );

            recordFailure(task, storageException);
            return;
        }

        try {
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
                        "Cleanup task is no longer owned by this worker: "
                                + "taskId={}",
                        task.id()
                );
            }
        } catch (Exception completionException) {
            log.error(
                    "Storage deletion succeeded but task completion "
                            + "could not be confirmed: taskId={}, "
                            + "exceptionType={}",
                    task.id(),
                    completionException.getClass().getSimpleName(),
                    completionException
            );

            // The task can be recovered when its lease expires.
        }
    }

    private void recordFailure(
            ReportStorageCleanupTaskStateService.ClaimedCleanupTask task,
            ReportStorageException storageException
    ) {
        try {
            boolean recorded = stateService.recordFailure(
                    task.id(),
                    task.token(),
                    storageException.getClass().getSimpleName()
            );

            if (!recorded) {
                log.warn(
                        "Cleanup failure was not recorded because the "
                                + "task lease is no longer owned: taskId={}",
                        task.id()
                );
            }
        } catch (Exception persistenceException) {
            log.error(
                    "Unable to persist cleanup failure; lease recovery "
                            + "will be relied upon: taskId={}, exceptionType={}",
                    task.id(),
                    persistenceException.getClass().getSimpleName(),
                    persistenceException
            );
        }
    }
}
