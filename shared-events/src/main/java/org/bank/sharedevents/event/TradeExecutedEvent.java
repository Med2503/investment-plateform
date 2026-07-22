package org.bank.sharedevents.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeExecutedEvent(
        UUID tradeId,
        String userId,
        UUID accountId,
        String symbol,
        BigDecimal quantity,
        BigDecimal price,
        TradeType tradeType,
        Instant executedAt
) {
}
