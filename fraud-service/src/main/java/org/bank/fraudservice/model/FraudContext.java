package org.bank.fraudservice.model;

import org.bank.sharedevents.event.trade.TradeExecutedEvent;

import java.math.BigDecimal;
import java.util.Objects;

public record FraudContext(
        TradeExecutedEvent trade,
        BigDecimal transactionAmount
) {

    public FraudContext {
        Objects.requireNonNull(trade, "trade must not be null");
        Objects.requireNonNull(
                transactionAmount,
                "transactionAmount must not be null"
        );

        if (transactionAmount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must not be negative"
            );
        }
    }
}
