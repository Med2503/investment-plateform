package org.bank.portfolioservice.consumer;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.portfolioservice.entity.ProcessedEvent;
import org.bank.portfolioservice.repository.ProcessedEventRepository;
import org.bank.portfolioservice.service.PortfolioService;
import org.bank.sharedevents.event.TradeExecutedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeEventConsumer {

    private final PortfolioService portfolioService;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(
            topics = "trade-executed",
            groupId = "portfolio-service-group"
    )
    @Transactional
    public void consume(TradeExecutedEvent event) {

        //log.info("Trade received : {} {} {}", event.tradeType(), event.symbol(), event.quantity());

        if (processedEventRepository.existsById(event.tradeId())) {
            log.warn("Trade event exists {}", event.tradeId());
            return;
        }

        portfolioService.processTradeEvent(event);

        ProcessedEvent processedEvent =
                ProcessedEvent.builder()
                        .eventId(event.tradeId())
                        .eventType("TRADE_EXECUTED")
                        .processedAt(Instant.now())
                        .build();

        processedEventRepository.save(processedEvent);
    }


}
