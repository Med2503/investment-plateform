package org.bank.portfolioservice.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record SellAssetRequest(
        String symbol,
        @DecimalMin("0.01")
        BigDecimal quantity
) {
}
