package org.bank.auditservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.auditservice.entity.AuditLog;
import org.bank.auditservice.event.ProcessedEvent;
import org.bank.auditservice.repository.AuditLogRepository;
import org.bank.auditservice.repository.ProcessedEventRepository;
import org.bank.sharedevents.event.audit.AuditEvent;
import org.bank.sharedevents.event.transfer.TransferCompletedEvent;
import org.bank.sharedevents.event.transfer.TransferFailedEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;
    private final ProcessedEventRepository processedEventRepository;


    public void save(AuditEvent event) {

        AuditLog log = AuditLog.builder()
                .eventType(event.getEventType())
                .accountId(event.getAccountId())
                .userId(event.getUserId())
                .timestamp(event.getTimestamp())
                .details(event.getDetails())
                .build();

        repository.save(log);
    }



    public void saveCompleted(TransferCompletedEvent event) {

        if (processedEventRepository.existsById(event.eventId())) {
            return;
        }

        AuditLog log = AuditLog.builder()
                .eventType("TRANSFER_COMPLETED")
                .accountId(event.sourceAccountId().toString())
                .timestamp(Instant.now())
                .details(
                        "transferId=" + event.transferId()
                                + ", sourceAccountId=" + event.sourceAccountId()
                                + ", destinationAccountId=" + event.destinationAccountId()
                                + ", amount=" + event.amount()
                                + ", currency=" + event.currency()
                )
                .build();
        repository.save(log);

        processedEventRepository.save(
                ProcessedEvent.builder()
                        .eventId(event.eventId())
                        .processedAt(Instant.now())
                        .build()
        );

    }


    public void saveFailure(TransferFailedEvent event) {

        if (processedEventRepository.existsById(event.eventId())) {
            return;
        }

        AuditLog log = AuditLog.builder()
                .eventType("TRANSFER_FAILED")
                .accountId(event.sourceAccountId().toString())
                .timestamp(Instant.now())
                .details(
                        "transferId=" + event.transferId()
                                + ", sourceAccountId=" + event.sourceAccountId()
                                + ", destinationAccountId=" + event.destinationAccountId()
                                + ", amount=" + event.amount()
                                + ", currency=" + event.currency()
                                + ", failureReason=" + event.failureReason()
                )
                .build();
        repository.save(log);

        processedEventRepository.save(
                ProcessedEvent.builder()
                        .eventId(event.eventId())
                        .processedAt(Instant.now())
                        .build()
        );
    }


}
