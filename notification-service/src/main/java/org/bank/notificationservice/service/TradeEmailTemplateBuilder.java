package org.bank.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Component
@RequiredArgsConstructor
public class TradeEmailTemplateBuilder
        implements EmailTemplateBuilder {


    private final SpringTemplateEngine templateEngine;


    @Override
    public String build(Object event) {


        TradeExecutedEvent tradeEvent =
                (TradeExecutedEvent) event;


        Context context = new Context();


        context.setVariable(
                "userName",
                tradeEvent.userId()
        );


        context.setVariable(
                "tradeType",
                tradeEvent.tradeType()
        );


        context.setVariable(
                "symbol",
                tradeEvent.symbol()
        );


        context.setVariable(
                "quantity",
                tradeEvent.quantity()
        );


        context.setVariable(
                "price",
                tradeEvent.executedPrice()
        );


        context.setVariable(
                "total",
                tradeEvent.executedPrice()
                        .multiply(
                                tradeEvent.quantity()
                        )
        );


        return templateEngine.process(
                "emails/trade-executed",
                context
        );

    }


}
