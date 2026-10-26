
package org.bank.reportingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import org.bank.sharedevents.event.trade.TradeType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "reporting_trade_records",
        indexes = {
                @Index(
                        name = "idx_reporting_trades_user_executed",
                        columnList = "user_id, executed_at"
                ),
                @Index(
                        name = "idx_reporting_trades_account_executed",
                        columnList = "account_id, executed_at"
                ),
                @Index(
                        name = "idx_reporting_trades_symbol",
                        columnList = "symbol"
                )
        }
)
public class TradeRecordEntity {

    @Id
    @Column(name = "trade_id", nullable = false, updatable = false)
    private UUID tradeId;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false, length = 20)
    private TradeType tradeType;

    @Column(name = "executed_price", nullable = false, precision = 24, scale = 8)
    private BigDecimal executedPrice;

    @Column(name = "executed_at", nullable = false)
    private Instant executedAt;

    protected TradeRecordEntity() {
        // Required by JPA.
    }

    public TradeRecordEntity(
            UUID tradeId,
            String userId,
            UUID accountId,
            String symbol,
            BigDecimal quantity,
            BigDecimal price,
            TradeType tradeType,
            BigDecimal executedPrice,
            Instant executedAt
    ) {
        this.tradeId = tradeId;
        this.userId = userId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.tradeType = tradeType;
        this.executedPrice = executedPrice;
        this.executedAt = executedAt;
    }

    public UUID getTradeId() {
        return tradeId;
    }

    public String getUserId() {
        return userId;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public TradeType getTradeType() {
        return tradeType;
    }

    public BigDecimal getExecutedPrice() {
        return executedPrice;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }
}
