package org.bank.reportingservice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reporting.storage.cleanup")
public record ReportStorageCleanupProperties(
        long fixedDelayMs,
        long leaseDurationSeconds,
        int maxAttempts,
        long initialDelaySeconds,
        long maxDelaySeconds,
        int batchSize
) {

    private static final long MAX_FIXED_DELAY_MS =
            86_400_000L;

    private static final long MAX_LEASE_DURATION_SECONDS =
            2_592_000L;

    private static final int MAX_ATTEMPTS = 100;

    private static final long MAX_BACKOFF_SECONDS =
            2_592_000L;

    private static final int MAX_BATCH_SIZE = 10_000;

    public ReportStorageCleanupProperties {
        if (fixedDelayMs <= 0
                || fixedDelayMs > MAX_FIXED_DELAY_MS) {
            throw new IllegalArgumentException(
                    "Cleanup fixed delay must be between 1 ms "
                            + "and 24 hours"
            );
        }

        if (leaseDurationSeconds <= 0
                || leaseDurationSeconds > MAX_LEASE_DURATION_SECONDS) {
            throw new IllegalArgumentException(
                    "Cleanup lease duration must be between 1 second "
                            + "and 30 days"
            );
        }

        if (maxAttempts <= 0 || maxAttempts > MAX_ATTEMPTS) {
            throw new IllegalArgumentException(
                    "Cleanup max attempts must be between 1 and 100"
            );
        }

        if (initialDelaySeconds <= 0
                || initialDelaySeconds > MAX_BACKOFF_SECONDS) {
            throw new IllegalArgumentException(
                    "Cleanup initial delay must be between 1 second "
                            + "and 30 days"
            );
        }

        if (maxDelaySeconds < initialDelaySeconds
                || maxDelaySeconds > MAX_BACKOFF_SECONDS) {
            throw new IllegalArgumentException(
                    "Cleanup max delay must be >= initial delay "
                            + "and <= 30 days"
            );
        }

        if (batchSize <= 0 || batchSize > MAX_BATCH_SIZE) {
            throw new IllegalArgumentException(
                    "Cleanup batch size must be between 1 and 10000"
            );
        }
    }
}
