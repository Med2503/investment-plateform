package org.bank.portfolioservice.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.portfolioservice.service.PortfolioService;
import org.bank.sharedevents.event.TradeExecutedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TradeEventConsumer {

    private final PortfolioService portfolioService;

    @KafkaListener(
            topics = "trade-executed",
            groupId = "portfolio-service"
    )
    public void consume(TradeExecutedEvent event) {

        log.info("Trade received : {} {} {}", event.tradeType(), event.symbol(), event.quantity());

        portfolioService.processTradeEvent(event);
    }


}
