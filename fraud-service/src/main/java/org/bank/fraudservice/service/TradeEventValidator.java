
package org.bank.fraudservice.service;

import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TradeEventValidator {

    public void validate(TradeExecutedEvent trade) {
        if (trade == null) {
            throw new IllegalArgumentException(
                    "Trade event must not be null"
            );
        }

        requireNonNull(trade.tradeId(), "tradeId");
        requireNonBlank(trade.userId(), "userId");
        requireNonNull(trade.accountId(), "accountId");
        requireNonBlank(trade.symbol(), "symbol");
        requirePositive(trade.quantity(), "quantity");
        requireNonNegative(trade.price(), "price");
        requireNonNull(trade.tradeType(), "tradeType");
        requireNonNegative(trade.executedPrice(), "executedPrice");
        requireNonNull(trade.executedAt(), "executedAt");
    }

    private void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(
                    field + " must not be null"
            );
        }
    }

    private void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " must not be blank"
            );
        }
    }

    private void requirePositive(BigDecimal value, String field) {
        requireNonNull(value, field);

        if (value.signum() <= 0) {
            throw new IllegalArgumentException(
                    field + " must be positive"
            );
        }
    }

    private void requireNonNegative(BigDecimal value, String field) {
        requireNonNull(value, field);

        if (value.signum() < 0) {
            throw new IllegalArgumentException(
                    field + " must not be negative"
            );
        }
    }
}
