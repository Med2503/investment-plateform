package org.bank.tradingservice.kafka.producer;


import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.TradeExecutedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;


    public void sendTradeExecuted(TradeExecutedEvent event) {
        kafkaTemplate.send("trade-executed", event);
    }


}
