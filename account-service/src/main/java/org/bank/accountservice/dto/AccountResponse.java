package org.bank.accountservice.dto;

import org.bank.accountservice.entity.AccountStatus;
import org.bank.accountservice.entity.AccountType;

import java.math.BigDecimal;

public record AccountResponse(

        String id,
        String accountNumber,
        String userId,
        BigDecimal balance,
        String currency,
        AccountType type,
        AccountStatus status

) {
}


