package org.bank.tradingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "trades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private String userId;


    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private String symbol;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType;


    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal quantity;

    @Column(precision = 19, scale = 4)
    private BigDecimal requestedPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal executedPrice;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeStatus status;

    private Instant createdAt;

    private Instant executedAt;

    @Column(length = 1000)
    private String failureReason;


}
