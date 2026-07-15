package org.bank.portfolioservice.exception;

public class InsufficientAssetQuantityException extends RuntimeException {
    public InsufficientAssetQuantityException(String message) {
        super(message);
    }
}
