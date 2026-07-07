package org.bank.accountservice.mapper;

import org.bank.accountservice.dto.AccountResponse;
import org.bank.accountservice.entity.Account;
import org.springframework.stereotype.Component;


@Component
public class AccountMapper {


    public AccountResponse toResponse(Account account) {
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
