
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
        Instant now = Instant.now(clock);

        Instant nextAttemptAt = now.plusSeconds(
                properties.initialDelaySeconds()
        );

        // The entity constructor preserves validation and error sanitization.
        ReportStorageCleanupTaskEntity task =
                new ReportStorageCleanupTaskEntity(
                        reportId,
                        objectKey,
                        nextAttemptAt,
                        errorMessage
                );

        // The native INSERT bypasses JPA callbacks, so timestamps
        // must be provided explicitly.
        taskRepository.insertIfAbsent(
                task.getId(),
                task.getReportId(),
                task.getObjectKey(),
                task.getStatus().name(),
                task.getAttempts(),
                task.getNextAttemptAt(),
                task.getLastError(),
                now,
                now,
                task.getVersion()
        );
    }
}
