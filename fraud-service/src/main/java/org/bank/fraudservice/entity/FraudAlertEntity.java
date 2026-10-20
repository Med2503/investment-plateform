package org.bank.fraudservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "fraud_alerts",
        indexes = {
                @Index(
                        name = "idx_fraud_alerts_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_fraud_alerts_trade_id",
                        columnList = "trade_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "trade_id", nullable = false, unique = true)
    private UUID tradeId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(name = "transaction_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal transactionAmount;

    @Column(name = "risk_score", nullable = false)
    private int riskScore;

    @Column(name = "fraud_status", nullable = false, length = 30)
    private String fraudStatus;

    @Column(name = "reasons", length = 1000)
    private String reasons;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}