
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReportStorageCleanupTaskService {

    private static final long INITIAL_DELAY_SECONDS = 30;

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final Clock clock;

    @Transactional
    public void enqueue(
            String objectKey,
            String reportId,
            String errorMessage
    ) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Object key must not be blank"
            );
        }

        if (taskRepository.existsByObjectKey(objectKey)) {
            return;
        }

        Instant nextAttemptAt = Instant.now(clock)
                .plusSeconds(INITIAL_DELAY_SECONDS);

        ReportStorageCleanupTaskEntity task =
                new ReportStorageCleanupTaskEntity(
                        reportId,
                        objectKey,
                        nextAttemptAt,
                        errorMessage
                );


        taskRepository.saveAndFlush(task);
    }
}
