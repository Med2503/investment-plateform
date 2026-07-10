package org.bank.auditservice.consumer;


import lombok.extern.slf4j.Slf4j;
import org.bank.auditservice.event.AuditEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuditEventConsumer {


    @KafkaListener(topics = "audit-events", groupId = "audit-group")
    public void consume(AuditEvent event) {


        log.info("AuditEvent received :{} ", event);
    }


}
