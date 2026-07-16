package org.bank.sharedevents.event;

import java.math.BigDecimal;
import java.time.Instant;

public record MarketPriceUpdatedEvent(
        String symbol,
        BigDecimal oldPrice,
        BigDecimal newPrice,
        String currency,
        Instant updatedAt
) {
}
