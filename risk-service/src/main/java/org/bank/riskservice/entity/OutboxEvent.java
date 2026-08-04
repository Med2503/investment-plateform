package org.bank.riskservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.bank.riskservice.model.OutboxStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEvent {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String aggregateType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private UUID correlationId;

    @Lob
    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private Integer retryCount;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant publishedAt;


    @PrePersist
    void prePersist() {
        createdAt = Instant.now();

        if (status == null) {
            status = OutboxStatus.PENDING;
        }
        if (retryCount == null) {
            retryCount = 0;
        }
    }


}
