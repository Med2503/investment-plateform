package org.bank.sharedevents.event.transfer;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferFailedEvent(
        UUID eventId,
        UUID transferId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        String failureReason
) {
}
