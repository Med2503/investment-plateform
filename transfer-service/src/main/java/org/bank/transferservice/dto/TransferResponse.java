package org.bank.transferservice.dto;

import org.bank.transferservice.entity.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(

        UUID id,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        TransferStatus status,
        Instant createdAt,
        Instant completedAt,
        String failureReason

) {
}
