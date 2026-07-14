package org.bank.portfolioservice.exception;

public class PortfolioAlreadyExistsException extends RuntimeException {
    public PortfolioAlreadyExistsException(String message) {
        super(message);
    }
}
