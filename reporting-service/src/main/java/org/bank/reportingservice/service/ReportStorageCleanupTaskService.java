
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.config.ReportStorageCleanupProperties;
import org.bank.reportingservice.entity.ReportStorageCleanupTaskEntity;
import org.bank.reportingservice.repository.ReportStorageCleanupTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ReportStorageCleanupTaskService {

    private final ReportStorageCleanupTaskRepository taskRepository;
    private final ReportStorageCleanupProperties properties;
    private final Clock clock;

    @Transactional
    public void enqueue(
            String objectKey,
            String reportId,
            String errorMessage
    ) {
        validateObjectKey(objectKey);

        if (taskRepository.existsByObjectKey(objectKey)) {
            return;
        }

        Instant nextAttemptAt = Instant.now(clock)
                .plusSeconds(properties.initialDelaySeconds());

        ReportStorageCleanupTaskEntity task =
                new ReportStorageCleanupTaskEntity(
                        reportId,
                        objectKey,
                        nextAttemptAt,
                        errorMessage
                );

        taskRepository.saveAndFlush(task);
    }

    private void validateObjectKey(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new IllegalArgumentException(
                    "Object key must not be blank"
            );
        }
    }
}
