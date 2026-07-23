package org.bank.tradingservice.execution;


import org.bank.sharedevents.event.TradeType;
import org.bank.tradingservice.entity.Trade;
import org.bank.tradingservice.exception.InvalidTradeException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TradeExecutionStrategyFactory {

    private final Map<TradeType, TradeExecutionStrategy> strategies;

    public TradeExecutionStrategyFactory(List<TradeExecutionStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        TradeExecutionStrategy::supports,
                        Function.identity()
                ));

    }

    public TradeExecutionStrategy getStrategy(TradeType type) {
        TradeExecutionStrategy strategy =
                strategies.get(type);
        if (strategy == null) {
            throw new InvalidTradeException("No strategy for trade type " + type);
        }
        return strategy;
    }
}
