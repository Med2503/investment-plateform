package org.bank.auditservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.sharedevents.event.AuditEvent;
import org.bank.auditservice.service.AuditService;
import org.bank.sharedevents.event.TransferCompletedEvent;
import org.bank.sharedevents.event.TransferFailedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditService auditService;


    @KafkaListener(topics = "audit-events", groupId = "audit-group")
    public void consume(AuditEvent event) {


        //log.info("AuditEvent received :{} ", event);

        auditService.save(event);
    }

    @KafkaListener(topics = "transfer-completed", groupId = "audit-group")
    public void consumeCompleted(TransferCompletedEvent event) {
        auditService.saveCompleted(event);
    }

    @KafkaListener(topics = "transfer-failed", groupId = "audit-group")
    public void consumeFailed(TransferFailedEvent event) {
        auditService.saveFailure(event);
    }

}
