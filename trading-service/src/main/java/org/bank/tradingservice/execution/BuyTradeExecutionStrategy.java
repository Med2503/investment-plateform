package org.bank.tradingservice.execution;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.bank.sharedevents.event.TradeType;
import org.bank.tradingservice.dto.request.CreateTradeRequest;
import org.bank.tradingservice.dto.response.MarketAssetResponse;
import org.bank.tradingservice.entity.Trade;
import org.bank.tradingservice.entity.TradeStatus;
import org.bank.tradingservice.exception.InvalidTradeException;
import org.bank.tradingservice.gateway.AccountGateway;
import org.bank.tradingservice.gateway.MarketDataGateway;
import org.bank.tradingservice.repository.TradeRepository;
import org.bank.tradingservice.service.OutboxService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class BuyTradeExecutionStrategy implements TradeExecutionStrategy {


    private final MarketDataGateway marketDataGateway;
    private final AccountGateway accountGateway;
    private final TradeRepository tradeRepository;
    private final OutboxService outboxService;


    @Override
    @Transactional
    public Trade execute(String userId, CreateTradeRequest request) {
        boolean moneyWithdrawn = false;
        try {
            MarketAssetResponse market = marketDataGateway.getPrice(request.symbol());

            if (market == null || market.currentPrice() == null || market.currentPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidTradeException("Invalid market price");
            }


            BigDecimal price = market.currentPrice();
            BigDecimal totalAmount = request.quantity().multiply(price);
            accountGateway.withdraw(request.accountId(), totalAmount);
            moneyWithdrawn = true;
            Trade trade = Trade.builder()
                    .orderId(UUID.randomUUID())
                    .userId(userId)
                    .accountId(request.accountId())
                    .symbol(request.symbol().toUpperCase())
                    .tradeType(TradeType.BUY)
                    .quantity(request.quantity())
                    .executedPrice(price)
                    .totalAmount(totalAmount)
                    .status(TradeStatus.EXECUTED)
                    .createdAt(Instant.now())
                    .executedAt(Instant.now())
                    .build();
            Trade saved = tradeRepository.save(trade);

            outboxService.saveTradeExecutedEvent(
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

        } catch (Exception ex) {
            if (moneyWithdrawn) {
                try {
                    accountGateway.deposit(
                            request.accountId(),
                            request.quantity().multiply(
                                    marketDataGateway.getPrice(request.symbol())
                                            .currentPrice()
                            )
                    );
                } catch (Exception compensationException) {
                // on vas logger apres avec Resil / ELK
                }
            }
            throw ex;
        }
    }

    @Override
    public TradeType supports() {
        return TradeType.BUY;
    }
}
