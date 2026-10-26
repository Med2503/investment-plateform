package org.bank.reportingservice.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;

import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.retries.DefaultRetryStrategy;
import software.amazon.awssdk.retries.api.RetryStrategy;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AmazonS3Config {

    @Bean
    public S3Client s3Client(
            @Value("${reporting.storage.s3.region}") String region,
            @Value("${reporting.storage.s3.connection-timeout-seconds:5}")
            long connectionTimeoutSeconds,
            @Value("${reporting.storage.s3.socket-timeout-seconds:30}")
            long socketTimeoutSeconds,
            @Value("${reporting.storage.s3.api-call-attempt-timeout-seconds:20}")
            long apiCallAttemptTimeoutSeconds,
            @Value("${reporting.storage.s3.api-call-timeout-seconds:60}")
            long apiCallTimeoutSeconds,
            @Value("${reporting.storage.s3.max-retries:3}")
            int maxRetries
    ) {
        validateConfiguration(
                region,
                connectionTimeoutSeconds,
                socketTimeoutSeconds,
                apiCallAttemptTimeoutSeconds,
                apiCallTimeoutSeconds,
                maxRetries
        );

        ClientOverrideConfiguration overrideConfiguration =
                ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(
                                Duration.ofSeconds(
                                        apiCallAttemptTimeoutSeconds
                                )
                        )
                        .apiCallTimeout(
                                Duration.ofSeconds(
                                        apiCallTimeoutSeconds
                                )
                        )
                        .retryStrategy(
                                DefaultRetryStrategy.standardStrategyBuilder()
                                        .maxAttempts(maxRetries + 1)
                                        .build()
                        )
                        .build();

        return S3Client.builder()
                .region(Region.of(region.trim()))
                .credentialsProvider(
                        DefaultCredentialsProvider.create()
                )
                .httpClientBuilder(
                        ApacheHttpClient.builder()
                                .connectionTimeout(
                                        Duration.ofSeconds(
                                                connectionTimeoutSeconds
                                        )
                                )
                                .socketTimeout(
                                        Duration.ofSeconds(
                                                socketTimeoutSeconds
                                        )
                                )
                )
                .overrideConfiguration(overrideConfiguration)
                .build();
    }

    private void validateConfiguration(
            String region,
            long connectionTimeoutSeconds,
            long socketTimeoutSeconds,
            long apiCallAttemptTimeoutSeconds,
            long apiCallTimeoutSeconds,
            int maxRetries
    ) {
        if (region == null || region.isBlank()) {
            throw new IllegalArgumentException(
                    "S3 region must not be blank"
            );
        }

        if (connectionTimeoutSeconds <= 0
                || socketTimeoutSeconds <= 0
                || apiCallAttemptTimeoutSeconds <= 0
                || apiCallTimeoutSeconds <= 0) {
            throw new IllegalArgumentException(
                    "S3 timeouts must be positive"
            );
        }

        if (apiCallAttemptTimeoutSeconds >= apiCallTimeoutSeconds) {
            throw new IllegalArgumentException(
                    "S3 attempt timeout must be less than total API call timeout"
            );
        }

        if (maxRetries < 0 || maxRetries == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "S3 max retries must be between 0 and "
                            + (Integer.MAX_VALUE - 1)
            );
        }
    }
}
