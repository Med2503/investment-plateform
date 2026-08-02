package org.bank.riskservice.scheduler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.riskservice.entity.OutboxEvent;
import org.bank.riskservice.messaging.OutboxPublisher;
import org.bank.riskservice.model.OutboxStatus;
import org.bank.riskservice.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {

    private final OutboxEventRepository repository;
    private final OutboxPublisher publisher;

    @Scheduled(
            fixedDelay = 5000
    )
    public void publishPendingEvents() {
        List<OutboxEvent> events = repository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (OutboxEvent event : events) {
            try {
                publisher.publish(event);
            } catch (Exception e) {
                log.error("Unable publishing event {} ", event.getId(), e);
            }
        }
    }
}
