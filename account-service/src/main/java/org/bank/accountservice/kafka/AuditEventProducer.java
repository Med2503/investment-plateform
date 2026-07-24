package org.bank.accountservice.kafka;


import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.audit.AuditEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditEventProducer {


    private static final String TOPIC = "audit-events";
    private final KafkaTemplate<String, AuditEvent> kafkaTemplate;

    public void publish(AuditEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
