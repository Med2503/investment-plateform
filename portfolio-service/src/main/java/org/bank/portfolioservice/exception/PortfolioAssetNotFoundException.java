package org.bank.portfolioservice.exception;

public class PortfolioAssetNotFoundException extends RuntimeException {
    public PortfolioAssetNotFoundException(String message) {
        super(message);
    }
}
