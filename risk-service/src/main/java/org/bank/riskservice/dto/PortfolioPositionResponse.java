package org.bank.riskservice.dto;

import java.math.BigDecimal;

public record PortfolioPositionResponse(
        String symbol,
        BigDecimal quantity
) {
}
