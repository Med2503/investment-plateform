package org.bank.tradingservice.execution;

import lombok.RequiredArgsConstructor;
import org.bank.tradingservice.dto.request.CreateTradeRequest;
import org.bank.tradingservice.entity.Trade;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TradeExecutionOrchestrator {

    private final TradeExecutionStrategyFactory factory;

    public Trade execute(String userId, CreateTradeRequest request) {
        TradeExecutionStrategy executionStrategy = factory.getStrategy(request.tradeType());
        return executionStrategy.execute(userId, request);
    }

}