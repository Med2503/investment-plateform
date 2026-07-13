package org.bank.transferservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.TransferCompletedEvent;
import org.bank.sharedevents.event.TransferFailedEvent;
import org.bank.transferservice.dto.CreateTransferRequest;
import org.bank.transferservice.dto.TransferResponse;
import org.bank.transferservice.entity.Transfer;
import org.bank.transferservice.entity.TransferStatus;
import org.bank.transferservice.exception.SourceAndDestinationMustBeDifferentException;
import org.bank.transferservice.mapper.TransferMapper;
import org.bank.transferservice.repository.TransferRepository;
import org.bank.transferservice.saga.TransferSagaOrchestrator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferMapper transferMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TransferSagaOrchestrator orchestrator;


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

    @Transactional
    public TransferResponse executeTransfer(UUID transferId) {

        Transfer transfer = transferRepository.findById(transferId).orElseThrow();

        try {

            orchestrator.execute(transfer);


            transfer.setStatus(TransferStatus.COMPLETED);

            transfer.setCompletedAt(
                    Instant.now()
            );

            Transfer saved = transferRepository.save(transfer);

            kafkaTemplate.send(
                    "transfer-completed",
                    new TransferCompletedEvent(
                            UUID.randomUUID(),
                            saved.getId(),
                            saved.getSourceAccountId(),
                            saved.getDestinationAccountId(),
                            saved.getAmount(),
                            saved.getCurrency()
                    )
            );
            return transferMapper.toResponse(saved);
        } catch (Exception e) {
            transfer.setStatus(TransferStatus.FAILED);
            transfer.setFailureReason(e.getMessage());
            Transfer saved = transferRepository.save(transfer);

            kafkaTemplate.send(
                    "transfer-failed",
                    new TransferFailedEvent(
                            UUID.randomUUID(),
                            saved.getId(),
                            saved.getSourceAccountId(),
                            saved.getDestinationAccountId(),
                            saved.getAmount(),
                            saved.getCurrency(),
                            saved.getFailureReason()
                    )
            );

            throw e;
        }


    }

}
