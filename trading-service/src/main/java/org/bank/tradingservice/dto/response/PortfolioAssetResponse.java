package org.bank.tradingservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record PortfolioAssetResponse(
        UUID id,

        String symbol,

        BigDecimal quantity,

        BigDecimal averagePrice
) {
}
