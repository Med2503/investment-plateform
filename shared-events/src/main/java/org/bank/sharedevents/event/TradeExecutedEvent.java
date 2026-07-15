package org.bank.sharedevents.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeExecutedEvent(
        UUID tradeId,
        String userId,
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        TradeType tradeType,
        Instant executedAt
) {
}
