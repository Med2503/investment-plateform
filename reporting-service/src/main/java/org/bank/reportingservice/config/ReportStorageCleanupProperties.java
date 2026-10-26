
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
    public ReportStorageCleanupProperties {
        if (fixedDelayMs <= 0) {
            throw new IllegalArgumentException(
                    "Cleanup fixed delay must be positive"
            );
        }
        if (leaseDurationSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Cleanup lease duration must be positive"
            );
        }
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException(
                    "Cleanup max attempts must be positive"
            );
        }
        if (initialDelaySeconds <= 0) {
            throw new IllegalArgumentException(
                    "Cleanup initial delay must be positive"
            );
        }
        if (maxDelaySeconds < initialDelaySeconds) {
            throw new IllegalArgumentException(
                    "Cleanup max delay must be >= initial delay"
            );
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException(
                    "Cleanup batch size must be positive"
            );
        }
    }
}
