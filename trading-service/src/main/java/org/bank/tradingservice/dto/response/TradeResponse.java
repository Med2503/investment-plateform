package org.bank.tradingservice.dto.response;

import org.bank.sharedevents.event.trade.TradeType;
import org.bank.tradingservice.entity.TradeStatus;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TradeResponse(
        UUID id,
        UUID orderId,
        String userId,
        UUID accountId,
        String symbol,
        TradeType tradeType,
        BigDecimal quantity,
        BigDecimal executedPrice,
        BigDecimal totalAmount,
        TradeStatus status,
        Instant createdAt,
        Instant executedAt
) {
}
