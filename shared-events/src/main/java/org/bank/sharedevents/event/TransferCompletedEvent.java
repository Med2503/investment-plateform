package org.bank.sharedevents.event;


import java.math.BigDecimal;
import java.util.UUID;

public record TransferCompletedEvent(
        UUID eventId,
        UUID transferId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency
) {
}
