package org.bank.tradingservice.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record BalanceOperationRequest(
        @DecimalMin("0.01")
        BigDecimal amount
) {
}
