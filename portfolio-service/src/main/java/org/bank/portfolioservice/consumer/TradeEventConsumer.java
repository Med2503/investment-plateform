package org.bank.portfolioservice.consumer;

import lombok.RequiredArgsConstructor;
import org.bank.portfolioservice.service.EventProcessingService;
import org.bank.portfolioservice.service.PortfolioService;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeEventConsumer {


    private final PortfolioService portfolioService;

    private final EventProcessingService eventProcessingService;



    @KafkaListener(
            topics = "trade_executed",
            groupId = "portfolio-service-group"
    )
    public void consume(
            TradeExecutedEvent event
    ){


        if(eventProcessingService
                .alreadyProcessed(event.tradeId())){


            return;
        }



        portfolioService.processTradeEvent(event);



        eventProcessingService.markProcessed(
                event.tradeId(),
                TradeExecutedEvent.class.getSimpleName()
        );

    }

}