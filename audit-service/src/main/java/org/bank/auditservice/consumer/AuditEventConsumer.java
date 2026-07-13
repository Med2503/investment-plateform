package org.bank.auditservice.consumer;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.auditservice.repository.ProcessedEventRepository;
import org.bank.sharedevents.event.AuditEvent;
import org.bank.auditservice.service.AuditService;
import org.bank.sharedevents.event.TransferCompletedEvent;
import org.bank.sharedevents.event.TransferFailedEvent;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditService auditService;
    private final ProcessedEventRepository processedEventRepository;


    @KafkaListener(topics = "audit-events", groupId = "audit-group")

    public void consume(AuditEvent event) {


        //log.info("AuditEvent received :{} ", event);

        auditService.save(event);
    }

    @Transactional
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 3000)

    )
    @KafkaListener(topics = "transfer-completed", groupId = "audit-group")

    public void consumeCompleted(TransferCompletedEvent event) {
        auditService.saveCompleted(event);
    }


    @Transactional
    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 3000)
    )
    @KafkaListener(topics = "transfer-failed", groupId = "audit-group")

    public void consumeFailed(TransferFailedEvent event) {
        auditService.saveFailure(event);
    }


    @DltHandler
    public void handleDlt(Object event) {
        log.error("Message moved to DLT :{} ", event);
    }

}
