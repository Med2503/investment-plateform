package org.bank.tradingservice.scheduler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.tradingservice.entity.OutboxEvent;
import org.bank.tradingservice.kafka.producer.TradeProducer;
import org.bank.tradingservice.repository.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final TradeProducer tradeProducer;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 2000)
    public void publishEvents() {

        List<OutboxEvent> events = outboxRepository.findEventsToPublish();

        for (OutboxEvent event : events) {


            try {

                if (event.getRetryCount() >= 10) {
                    log.error("OutboxEvent {} reached max count", event.getId());
                }


                publish(event);
            } catch (Exception ex) {
                event.setRetryCount(event.getRetryCount() + 1);
                event.setLastRetryAt(Instant.now());
                outboxRepository.save(event);
                log.error("unable to publish OutboxEvent{}", event.getId(), ex);
            }

        }

    }

    private void publish(OutboxEvent outboxEvent) throws JsonProcessingException {
        TradeExecutedEvent event = objectMapper.readValue(
                outboxEvent.getPayload(),
                TradeExecutedEvent.class
        );

        tradeProducer.sendTradeExecuted(event);

        outboxEvent.setPublished(true);
        outboxEvent.setPublishedAt(Instant.now());
        outboxRepository.save(outboxEvent);
    }
}
