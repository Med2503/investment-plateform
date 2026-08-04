package org.bank.riskservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.bank.riskservice.model.DecisionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "risk_decisions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiskDecisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    private String userId;

    private String symbol;
    private BigDecimal amount;

    @Column(nullable = false)
    private UUID correlationId;

    @Enumerated(EnumType.STRING)
    private DecisionStatus status;

    private String rejectionReason;
    private String rejectionMessage;

    private Instant createdAt;


    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }
}
