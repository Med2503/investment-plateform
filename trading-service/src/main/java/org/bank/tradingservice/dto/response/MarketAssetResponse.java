package org.bank.tradingservice.dto.response;

import java.math.BigDecimal;

public record MarketAssetResponse(
        String symbol,
        BigDecimal currentPrice
) {
}
