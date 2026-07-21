package org.bank.tradingservice.exception;

public class TradeAlreadyExecutedException extends RuntimeException {
    public TradeAlreadyExecutedException(String message) {
        super(message);
    }
}
