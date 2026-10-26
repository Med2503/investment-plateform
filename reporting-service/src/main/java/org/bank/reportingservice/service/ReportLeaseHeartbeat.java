package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportLeaseHeartbeat {

    private final ReportLeaseService leaseService;
    private final ThreadPoolTaskScheduler taskScheduler;

    @Value("${reporting.processing.heartbeat-interval-seconds:60}")
    private long heartbeatIntervalSeconds;

    public Heartbeat start(UUID reportId, UUID token) {
        if (heartbeatIntervalSeconds <= 0) {
            throw new IllegalStateException(
                    "reporting.processing.heartbeat-interval-seconds "
                            + "must be greater than zero"
            );
        }

        AtomicBoolean leaseLost = new AtomicBoolean(false);

        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                () -> {
                    try {
                        if (!leaseService.renew(reportId, token)) {
                            leaseLost.set(true);
                            log.warn(
                                    "Lease lost for report {}",
                                    reportId
                            );
                        }
                    } catch (Exception exception) {
                        leaseLost.set(true);
                        log.error(
                                "Heartbeat failed for report {}",
                                reportId,
                                exception
                        );
                    }
                },
                Duration.ofSeconds(heartbeatIntervalSeconds)
        );

        if (future == null) {
            throw new IllegalStateException(
                    "Could not schedule report lease heartbeat"
            );
        }

        return new Heartbeat(future, leaseLost);
    }

    public record Heartbeat(
            ScheduledFuture<?> future,
            AtomicBoolean leaseLost
    ) implements AutoCloseable {

        @Override
        public void close() {
            future.cancel(false);
        }
    }
}