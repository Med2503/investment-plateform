package org.bank.tradingservice.execution;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.TradeExecutedEvent;
import org.bank.tradingservice.dto.response.MarketAssetResponse;
import org.bank.tradingservice.entity.Trade;
import org.bank.tradingservice.entity.TradeStatus;
import org.bank.tradingservice.exception.InvalidTradeException;
import org.bank.tradingservice.gateway.AccountGateway;
import org.bank.tradingservice.gateway.MarketDataGateway;
import org.bank.tradingservice.kafka.producer.TradeProducer;
import org.bank.tradingservice.repository.TradeRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TradeExecutionOrchestrator {

    private final TradeRepository tradeRepository;
    private final MarketDataGateway marketDataGateway;
    private final AccountGateway accountGateway;
    private final TradeProducer tradeProducer;

    @Transactional
    public Trade execute(Trade trade) {


        if (trade.getStatus() != TradeStatus.PENDING) {
            throw new InvalidTradeException("Only pending trades can be executed");
        }

        MarketAssetResponse marketAssetResponse = marketDataGateway.getPrice(trade.getSymbol());

        BigDecimal price = marketAssetResponse.currentPrice();

        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTradeException("Invalid market price");
        }

        BigDecimal totalAmount = trade.getQuantity().multiply(price);

        accountGateway.withdraw(trade.getAccountId(), totalAmount);

        trade.setExecutedPrice(price);
        trade.setTotalAmount(totalAmount);
        trade.setStatus(TradeStatus.EXECUTED);
        trade.setExecutedAt(Instant.now());

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
}
