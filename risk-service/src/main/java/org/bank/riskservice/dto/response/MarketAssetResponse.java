package org.bank.riskservice.dto.response;

import java.math.BigDecimal;

public record MarketAssetResponse(
        String symbol,
        BigDecimal currentPrice
) {
}
