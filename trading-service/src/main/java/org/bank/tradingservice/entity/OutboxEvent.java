package org.bank.tradingservice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events",
        indexes = {
                @Index(
                        name = "idx_outbox_publish_created",
                        columnList = "published,createdAt"
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;


    @Builder.Default
    @Column(nullable = false)
    private Boolean published = false;

    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant publishedAt;


    @Builder.Default
    @Column(nullable = false)
    private Integer retryCount = 0;

    private Instant lastRetryAt;

}
