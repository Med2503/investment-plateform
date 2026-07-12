package org.bank.auditservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.auditservice.entity.AuditLog;
import org.bank.auditservice.repository.AuditLogRepository;
import org.bank.sharedevents.event.AuditEvent;
import org.springframework.stereotype.Service;

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


}
