
package org.bank.reportingservice.service;

import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TradeEventValidator {

    public void validate(TradeExecutedEvent event) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Trade event must not be null"
            );
        }

        requireNonNull(event.tradeId(), "tradeId");
        requireNonBlank(event.userId(), "userId");
        requireNonNull(event.accountId(), "accountId");
        requireNonBlank(event.symbol(), "symbol");

        requirePositive(event.quantity(), "quantity");
        requireNonNegative(event.price(), "price");
        requireNonNull(event.tradeType(), "tradeType");
        requireNonNegative(event.executedPrice(), "executedPrice");
        requireNonNull(event.executedAt(), "executedAt");
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
                    field + " must be greater than zero"
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
