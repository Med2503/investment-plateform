package org.bank.reportingservice.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Duration;

@Configuration
public class AmazonS3Config {

    @Bean
    public S3Client s3Client(
            @Value("${reporting.storage.s3.region}") String region,
            @Value("${reporting.storage.s3.connection-timeout-seconds:5}")
            long connectionTimeoutSeconds,
            @Value("${reporting.storage.s3.socket-timeout-seconds:30}")
            long socketTimeoutSeconds,
            @Value("${reporting.storage.s3.api-call-timeout-seconds:60}")
            long apiCallTimeoutSeconds
    ) {
        validateTimeouts(
                connectionTimeoutSeconds,
                socketTimeoutSeconds,
                apiCallTimeoutSeconds
        );

        ClientOverrideConfiguration overrideConfiguration =
                ClientOverrideConfiguration.builder()
                        .apiCallTimeout(
                                Duration.ofSeconds(apiCallTimeoutSeconds)
                        )
                        .apiCallAttemptTimeout(
                                Duration.ofSeconds(apiCallTimeoutSeconds)
                        )
                        .build();

        return S3Client.builder()
                .region(Region.of(region))
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

    private void validateTimeouts(
            long connectionTimeoutSeconds,
            long socketTimeoutSeconds,
            long apiCallTimeoutSeconds
    ) {
        if (connectionTimeoutSeconds <= 0
                || socketTimeoutSeconds <= 0
                || apiCallTimeoutSeconds <= 0) {
            throw new IllegalArgumentException(
                    "S3 timeouts must be positive"
            );
        }

        if (apiCallTimeoutSeconds < socketTimeoutSeconds) {
            throw new IllegalArgumentException(
                    "S3 API call timeout must be >= socket timeout"
            );
        }
    }
}
