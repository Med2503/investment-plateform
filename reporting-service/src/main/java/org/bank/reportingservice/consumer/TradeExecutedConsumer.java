
package org.bank.reportingservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.bank.reportingservice.entity.TradeRecordEntity;
import org.bank.reportingservice.repository.TradeRecordRepository;
import org.bank.reportingservice.service.TradeEventValidator;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeExecutedConsumer {

    private final TradeRecordRepository tradeRecordRepository;
    private final TradeEventValidator tradeEventValidator;

    @KafkaListener(
            topics = kafkaTopics.TRADE_EXECUTED,
            groupId = "${spring.kafka.consumer.group-id:reporting-service}"
    )
    @Transactional
    public void consume(TradeExecutedEvent event) {
        tradeEventValidator.validate(event);

        if (tradeRecordRepository.existsById(event.tradeId())) {
            log.debug(
                    "Duplicate trade event ignored: tradeId={}",
                    event.tradeId()
            );
            return;
        }

        TradeRecordEntity record = new TradeRecordEntity(
                event.tradeId(),
                event.userId(),
                event.accountId(),
                event.symbol().trim(),
                event.quantity(),
                event.price(),
                event.tradeType(),
                event.executedPrice(),
                event.executedAt()
        );

        tradeRecordRepository.save(record);

        log.info(
                "Trade recorded for reporting: tradeId={}, userId={}",
                event.tradeId(),
                event.userId()
        );
    }
}
