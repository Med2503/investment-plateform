package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.reportingservice.service.ReportLeaseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportLeaseHeartbeat {

    private final ReportLeaseService leaseService;
    private final ThreadPoolTaskScheduler taskScheduler;

    @Value("${reporting.processing.heartbeat-interval-seconds:60}")
    private long heartbeatIntervalSeconds;

    @Value("${reporting.processing.heartbeat-max-failures:2}")
    private int maxConsecutiveFailures;

    public Heartbeat start(UUID reportId, UUID token) {
        Objects.requireNonNull(reportId, "Report ID must not be null");
        Objects.requireNonNull(token, "Lease token must not be null");

        if (heartbeatIntervalSeconds <= 0) {
            throw new IllegalStateException(
                    "Heartbeat interval must be positive"
            );
        }

        if (maxConsecutiveFailures < 1) {
            throw new IllegalStateException(
                    "Heartbeat maximum failures must be at least one"
            );
        }

        AtomicBoolean leaseLost = new AtomicBoolean(false);
        AtomicInteger consecutiveFailures = new AtomicInteger(0);

        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                () -> renew(
                        reportId,
                        token,
                        leaseLost,
                        consecutiveFailures
                ),
                Duration.ofSeconds(heartbeatIntervalSeconds)
        );

        if (future == null) {
            throw new IllegalStateException(
                    "Could not schedule heartbeat for report " + reportId
            );
        }

        log.debug("Heartbeat started for report {}", reportId);

        return new Heartbeat(future, leaseLost);
    }

    private void renew(
            UUID reportId,
            UUID token,
            AtomicBoolean leaseLost,
            AtomicInteger consecutiveFailures
    ) {
        if (leaseLost.get()) {
            return;
        }

        try {
            boolean renewed = leaseService.renew(reportId, token);

            if (!renewed) {
                leaseLost.set(true);

                log.warn(
                        "Lease renewal rejected for report {}. "
                                + "Worker must stop processing.",
                        reportId
                );
                return;
            }

            consecutiveFailures.set(0);

            log.debug("Lease renewed for report {}", reportId);
        } catch (Exception exception) {
            int failures = consecutiveFailures.incrementAndGet();

            log.warn(
                    "Heartbeat failed for report {} ({}/{} consecutive failures)",
                    reportId,
                    failures,
                    maxConsecutiveFailures,
                    exception
            );

            if (failures >= maxConsecutiveFailures) {
                leaseLost.set(true);

                log.error(
                        "Heartbeat failure threshold reached for report {}. "
                                + "Worker must stop processing.",
                        reportId
                );
            }
        }
    }

    public record Heartbeat(
            ScheduledFuture<?> future,
            AtomicBoolean leaseLost
    ) implements AutoCloseable {

        public Heartbeat {
            Objects.requireNonNull(future, "Scheduled future must not be null");
            Objects.requireNonNull(leaseLost, "Lease state must not be null");
        }

        @Override
        public void close() {
            future.cancel(false);
        }
    }
}
