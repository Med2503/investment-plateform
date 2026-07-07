package org.bank.accountservice.dto;

import org.bank.accountservice.entity.AccountType;

import java.math.BigDecimal;

public record CreateAccountRequest(String userId,
                                   AccountType type,
                                   String currency,
                                   BigDecimal initialDeposit) {


}
