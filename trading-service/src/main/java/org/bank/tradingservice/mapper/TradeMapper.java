package org.bank.tradingservice.mapper;


import org.bank.tradingservice.dto.response.TradeResponse;
import org.bank.tradingservice.entity.Trade;
import org.springframework.stereotype.Component;

@Component
public class TradeMapper {

    public TradeResponse toResponse(Trade trade) {

        return new TradeResponse(
                trade.getId(),
                trade.getOrderId(),
                trade.getUserId(),
                trade.getAccountId(),
                trade.getSymbol(),
                trade.getTradeType(),
                trade.getQuantity(),
                trade.getExecutedPrice(),
                trade.getTotalAmount(),
                trade.getStatus(),
                trade.getCreatedAt(),
                trade.getExecutedAt()
        );

    }
}
