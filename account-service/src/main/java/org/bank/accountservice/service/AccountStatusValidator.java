package org.bank.accountservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.accountservice.entity.Account;
import org.bank.accountservice.entity.AccountStatus;
import org.bank.accountservice.exception.AccountNotFoundException;
import org.bank.accountservice.repository.AccountRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountStatusValidator {

    private final AccountRepository accountRepository;


    public void validate(AccountStatus current, AccountStatus next) {


        if (current == AccountStatus.CLOSED) {
            throw new IllegalStateException("Status closed ! no change status permitted");
        }

        if (current == AccountStatus.BLOCKED && next == AccountStatus.CLOSED) {
            return;

        }
        if (current == AccountStatus.ACTIVE && next == AccountStatus.PENDING) {
            throw new IllegalStateException("Status active cannot be changed to pending");
        }
    }

    public void validateActiveStatus(UUID accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("account not found!")
        );
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalStateException(("Account is not active"));
        }
    }
}
