package org.bank.portfolioservice.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_processed_event",
                        columnNames = "event_id"
                )
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(
            name = "event_id",
            nullable = false,
            unique = true
    )
    private UUID eventId;

    @Column(nullable = false)
    private String eventType;
    @Column(nullable = false)
    private Instant processedAt;

}
