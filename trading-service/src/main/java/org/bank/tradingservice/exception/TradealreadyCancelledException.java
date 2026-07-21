package org.bank.tradingservice.exception;

public class TradealreadyCancelledException extends RuntimeException {
    public TradealreadyCancelledException(String message) {
        super(message);
    }
}
