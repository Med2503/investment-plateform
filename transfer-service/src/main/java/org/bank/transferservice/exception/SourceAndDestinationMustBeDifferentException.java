package org.bank.transferservice.exception;

public class SourceAndDestinationMustBeDifferentException extends RuntimeException {
    public SourceAndDestinationMustBeDifferentException(String message) {
        super(message);
    }
}
