package org.bank.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.bank.accountservice.entity.AccountType;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotNull(message = "Account type is required")
        AccountType type,
        @NotBlank(message = "currency is required")
        String currency,
        @PositiveOrZero(message = "Initial deposit must be positive or zero")
        BigDecimal initialDeposit) {


}
