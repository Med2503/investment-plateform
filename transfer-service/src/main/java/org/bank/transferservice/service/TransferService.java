package org.bank.transferservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.transferservice.dto.CreateTransferRequest;
import org.bank.transferservice.dto.TransferResponse;
import org.bank.transferservice.entity.Transfer;
import org.bank.transferservice.entity.TransferStatus;
import org.bank.transferservice.exception.SourceAndDestinationMustBeDifferentException;
import org.bank.transferservice.mapper.TransferMapper;
import org.bank.transferservice.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;


    @Transactional
    public TransferResponse createTransfer(CreateTransferRequest request) {

        if (request.sourceAccountId().equals(request.destinationAccountId())) {
            throw new SourceAndDestinationMustBeDifferentException("Source and Destination accounts must be different");
        }

        Transfer transfer = Transfer
                .builder()
                .sourceAccountId(request.sourceAccountId())
                .destinationAccountId(request.destinationAccountId())
                .amount(request.amount())
                .currency(request.currency().toUpperCase())
                .status(TransferStatus.PENDING)
                .createdAt(Instant.now())
                .build();
        Transfer savedTransfer = transferRepository.save(transfer);

        return transferMapper.toResponse(savedTransfer);

    }

}
