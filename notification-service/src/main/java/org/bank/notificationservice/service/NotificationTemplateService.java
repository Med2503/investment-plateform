package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class NotificationTemplateService {


    private final TradeEmailTemplateBuilder tradeBuilder;


    public String buildTradeExecutedEmail(
            TradeExecutedEvent event
    ) {

        return tradeBuilder.build(event);

    }


}