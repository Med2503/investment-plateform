package org.bank.accountservice.dto;

import org.bank.accountservice.entity.AccountType;

import java.math.BigDecimal;

public record CreateAccountRequest(
        AccountType type,
        String currency,
        BigDecimal initialDeposit) {


}
