package org.bank.accountservice.service;


import org.bank.accountservice.entity.AccountStatus;
import org.springframework.stereotype.Component;

@Component
public class AccountStatusValidator {

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
}
