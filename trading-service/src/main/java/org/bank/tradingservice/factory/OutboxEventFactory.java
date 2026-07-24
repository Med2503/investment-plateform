package org.bank.tradingservice.factory;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.tradingservice.entity.AggregateType;
import org.bank.tradingservice.entity.OutboxEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventFactory {
    private final ObjectMapper objectMapper;

    public OutboxEvent createTradeExecutedEvent(TradeExecutedEvent event) {
        try {
            return OutboxEvent.builder()
                    .aggregateType(AggregateType.TRADE)
                    .aggregateId(event.tradeId())
                    .eventType(TradeExecutedEvent.class.getSimpleName())
                    .payload(objectMapper.writeValueAsString(event))
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("couldn't serialize TradeExecutedEvent", e);
        }
    }
}
