package org.bank.riskservice.model;


import lombok.Builder;
import org.bank.sharedevents.event.trade.TradeType;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record RiskContext(
        String userId,
        UUID accountId,
        String symbol,
        TradeType tradeType,
        BigDecimal quantity,
        BigDecimal currentPrice,
        BigDecimal totalAmount,
        RiskProfile riskProfile


) {

    public boolean isBuy() {
        return tradeType == TradeType.BUY;
    }

    public boolean isSell() {
        return tradeType == TradeType.SELL;
    }
}
