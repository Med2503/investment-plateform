package org.bank.sharedevents.event.trade;

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
        BigDecimal executedPrice,
        Instant executedAt
) {
}
