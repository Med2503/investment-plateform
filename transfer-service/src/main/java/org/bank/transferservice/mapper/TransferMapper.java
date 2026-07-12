package org.bank.transferservice.mapper;


import org.bank.transferservice.dto.TransferResponse;
import org.bank.transferservice.entity.Transfer;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

    public TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getSourceAccountId(),
                transfer.getDestinationAccountId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getStatus(),
                transfer.getCreatedAt(),
                transfer.getCompletedAt(),
                transfer.getFailureReason()
        );
    }
}
