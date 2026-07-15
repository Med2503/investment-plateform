package org.bank.marketdataservice.exception;

public class InvalidMarketPriceException extends RuntimeException {
    public InvalidMarketPriceException(String message) {
        super(message);
    }
}
