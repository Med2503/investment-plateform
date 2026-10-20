
package org.bank.fraudservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.fraudservice.model.FraudAnalysisResult;
import org.bank.fraudservice.service.FraudDetectionService;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeExecutedConsumer {

    private final FraudDetectionService fraudDetectionService;

    @KafkaListener(
            topics = kafkaTopics.TRADE_EXECUTED,
            groupId = "${spring.kafka.consumer.group-id:fraud-service}"
    )
    public void consume(TradeExecutedEvent event) {
        log.info(
                "Received trade execution event: tradeId={}, userId={}",
                event.tradeId(),
                event.userId()
        );

        FraudAnalysisResult result = fraudDetectionService.analyze(event);

        log.info(
                "Fraud analysis completed: tradeId={}, status={}, score={}",
                event.tradeId(),
                result.status(),
                result.riskScore()
        );
    }
}
