package org.bank.auditservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.auditservice.entity.AuditLog;
import org.bank.auditservice.repository.AuditLogRepository;
import org.bank.sharedevents.event.AuditEvent;
import org.bank.sharedevents.event.TransferCompletedEvent;
import org.bank.sharedevents.event.TransferFailedEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;


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
    }

    public void saveFailure(TransferFailedEvent event) {

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
    }


}
