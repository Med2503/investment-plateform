package org.bank.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "failed_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String topic;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Builder.Default
    @Column(nullable = false)
    private Instant failedAt = Instant.now();


    @Column(nullable = false)
    private String eventType;

    @Builder.Default
    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column(length = 3000)
    private String lastError;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FailedNotificationStatus status = FailedNotificationStatus.PENDING;

    private Instant lastRetryAt;
    private Instant completedAt;


    @Builder.Default
    private Integer maxRetry = 5;

    @Builder.Default
    private Instant nextRetryAt = Instant.now();

}
