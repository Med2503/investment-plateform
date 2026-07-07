package org.bank.accountservice.mapper;

import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.entity.Account;

public class AccountMapper {


    public static AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getUserId(),
                account.getBalance(),
                account.getCurrency(),
                account.getType().name(),
                account.getStatus().name()
        );
    }


}
