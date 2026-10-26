
package org.bank.reportingservice.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.entity.CleanupTaskStatus;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.bank.reportingservice.service.ReportStorageCleanupRetryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportStorageCleanupRetryScheduler {

    private static final int BATCH_SIZE = 50;

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final ReportStorageCleanupRetryService retryService;

    @Scheduled(
            fixedDelayString =
                    "${reporting.storage.cleanup.fixed-delay-ms:30000}"
    )
    public void retryFailedCleanups() {
        List<ReportStorageCleanupTaskEntity> tasks =
                taskRepository
                        .findByStatusAndNextAttemptAtLessThanEqualOrderByNextAttemptAtAsc(
                                CleanupTaskStatus.PENDING,
                                Instant.now(),
                                PageRequest.of(0, BATCH_SIZE)
                        );

        for (ReportStorageCleanupTaskEntity task : tasks) {
            try {
                retryService.processTask(task.getId());
            } catch (Exception exception) {
                log.error(
                        "Could not process cleanup task {}",
                        task.getId(),
                        exception
                );
            }
        }
    }
}
