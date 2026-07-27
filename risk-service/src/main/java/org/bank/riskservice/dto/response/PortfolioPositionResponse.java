package org.bank.riskservice.dto.response;

import java.math.BigDecimal;

public record PortfolioPositionResponse(
        String symbol,
        BigDecimal quantity
) {
}
