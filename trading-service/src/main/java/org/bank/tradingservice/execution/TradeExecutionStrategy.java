package org.bank.tradingservice.execution;

import org.bank.sharedevents.event.trade.TradeType;
import org.bank.tradingservice.dto.request.CreateTradeRequest;
import org.bank.tradingservice.entity.Trade;

public interface TradeExecutionStrategy {

    Trade execute(String userId, CreateTradeRequest request);

    TradeType supports();
}
