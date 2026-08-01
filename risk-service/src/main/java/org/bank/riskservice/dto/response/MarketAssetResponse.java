package org.bank.riskservice.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record MarketAssetResponse(

        UUID id,
        String symbol,
        String name,
        BigDecimal currentPrice,
        String currency,
        BigDecimal volatility,
        Instant lastUpdated

) {
}
