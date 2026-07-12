package org.bank.sharedevents.event;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferFailedEvent(
        UUID transferId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        String failureReason
) {
}
