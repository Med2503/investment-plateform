
package org.bank.reportingservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.bank.reportingservice.entity.TradeRecordEntity;
import org.bank.reportingservice.repository.TradeRecordRepository;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeExecutedConsumer {

    private final TradeRecordRepository tradeRecordRepository;

    @KafkaListener(
            topics = kafkaTopics.TRADE_EXECUTED,
            groupId = "${spring.kafka.consumer.group-id:reporting-service}"
    )
    @Transactional
    public void consume(TradeExecutedEvent event) {
        if (event == null || event.tradeId() == null) {
            throw new IllegalArgumentException(
                    "Trade event and tradeId must not be null"
            );
        }

        if (tradeRecordRepository.existsById(event.tradeId())) {
            log.debug(
                    "Trade event already processed: tradeId={}",
                    event.tradeId()
            );
            return;
        }

        TradeRecordEntity record = new TradeRecordEntity(
                event.tradeId(),
                event.userId(),
                event.accountId(),
                event.symbol(),
                event.quantity(),
                event.price(),
                event.tradeType(),
                event.executedPrice(),
                event.executedAt()
        );

        try {
            tradeRecordRepository.saveAndFlush(record);
            log.info(
                    "Trade recorded for reporting: tradeId={}, userId={}",
                    event.tradeId(),
                    event.userId()
            );
        } catch (DataIntegrityViolationException exception) {
            /*
             * Another consumer may have inserted the same trade
             * concurrently. Re-throwing lets the transaction roll back.
             */
            throw exception;
        }
    }
}
