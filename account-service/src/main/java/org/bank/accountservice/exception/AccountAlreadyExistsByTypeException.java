package org.bank.accountservice.exception;

public class AccountAlreadyExistsByTypeException extends RuntimeException {
    public AccountAlreadyExistsByTypeException(String message) {
        super(message);
    }
}
