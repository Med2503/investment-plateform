package org.bank.tradingservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.TradeExecutedEvent;
import org.bank.tradingservice.entity.OutboxEvent;
import org.bank.tradingservice.factory.OutboxEventFactory;
import org.bank.tradingservice.repository.OutboxRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final OutboxEventFactory outboxEventFactory;

    public void saveTradeExecutedEvent(TradeExecutedEvent event) {
        OutboxEvent outboxEvent = outboxEventFactory.createTradeExecutedEvent(event);
        outboxRepository.save(outboxEvent);
    }
}
