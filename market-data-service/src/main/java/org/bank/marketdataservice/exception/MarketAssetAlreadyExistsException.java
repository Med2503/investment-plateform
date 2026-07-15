package org.bank.marketdataservice.exception;

public class MarketAssetAlreadyExistsException extends RuntimeException {
    public MarketAssetAlreadyExistsException(String message) {
        super(message);
    }
}
