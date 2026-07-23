package org.bank.tradingservice.execution;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.TradeExecutedEvent;
import org.bank.sharedevents.event.TradeType;
import org.bank.tradingservice.dto.request.CreateTradeRequest;
import org.bank.tradingservice.dto.response.MarketAssetResponse;
import org.bank.tradingservice.entity.Trade;
import org.bank.tradingservice.entity.TradeStatus;
import org.bank.tradingservice.gateway.AccountGateway;
import org.bank.tradingservice.gateway.MarketDataGateway;
import org.bank.tradingservice.kafka.producer.TradeProducer;
import org.bank.tradingservice.repository.TradeRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SellTradeExecutionStrategy implements TradeExecutionStrategy {

    private final MarketDataGateway marketDataGateway;
    private final AccountGateway accountGateway;
    private final TradeRepository tradeRepository;
    private final TradeProducer tradeProducer;

    @Override
    @Transactional
    public Trade execute(String userId, CreateTradeRequest request) {

        //TODO

        MarketAssetResponse market = marketDataGateway.getPrice(request.symbol());
        BigDecimal price = market.currentPrice();
        BigDecimal totalAmount = request.quantity().multiply(price);
        accountGateway.deposit(request.accountId(), totalAmount);
        Trade trade = Trade.builder()
                .orderId(UUID.randomUUID())
                .userId(userId)
                .accountId(request.accountId())
                .symbol(request.symbol().toUpperCase())
                .tradeType(TradeType.SELL)
                .quantity(request.quantity())
                .executedPrice(price)
                .totalAmount(totalAmount)
                .status(TradeStatus.EXECUTED)
                .createdAt(Instant.now())
                .executedAt(Instant.now())
                .build();
        Trade saved = tradeRepository.save(trade);
        tradeProducer.sendTradeExecuted(
                new TradeExecutedEvent(
                        saved.getId(),

                        saved.getUserId(),

                        saved.getAccountId(),

                        saved.getSymbol(),

                        saved.getQuantity(),

                        saved.getExecutedPrice(),

                        saved.getTradeType(),

                        saved.getExecutedAt()
                )
        );

        return saved;
    }

    @Override
    public TradeType supports() {
        return TradeType.SELL;
    }
}
