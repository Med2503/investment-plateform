package org.bank.tradingservice.kafka.producer;


import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.tradingservice.kafka.topics.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopics kafkaTopics;

    public void sendTradeExecuted(TradeExecutedEvent event) {


        kafkaTemplate.send(KafkaTopics.TRADE_EXECUTED, event.tradeId().toString(), event);
    }


}
