package org.bank.marketdataservice.exception;

public class MarketAssetNotFoundException extends RuntimeException {
    public MarketAssetNotFoundException(String message) {
        super(message);
    }
}
