package org.bank.riskservice.dto;

import java.math.BigDecimal;

public record MarketAssetResponse(
        String symbol,
        BigDecimal currentPrice
) {
}
