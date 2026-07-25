package org.bank.notificationservice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "processed_events",
        uniqueConstraints = @UniqueConstraint(columnNames = "eventId")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String eventType;

    @Builder.Default
    private Instant processedAt = Instant.now();

}