package org.bank.accountservice.exception;

public class AccountExistsByUserIdAndType extends RuntimeException {
    public AccountExistsByUserIdAndType(String message) {
        super(message);
    }
}
