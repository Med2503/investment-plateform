package org.bank.accountservice.dto;

import java.math.BigDecimal;

public record BalanceOperationRequest(
        BigDecimal amount
) {
}
