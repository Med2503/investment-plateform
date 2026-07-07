package org.bank.accountservice.exception;

public class AccountCurrencyException extends RuntimeException {
    public AccountCurrencyException(String message) {
        super(message);
    }
}
