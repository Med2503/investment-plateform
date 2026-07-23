package org.bank.tradingservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.tradingservice.dto.request.CreateTradeRequest;
import org.bank.tradingservice.dto.response.TradeResponse;
import org.bank.tradingservice.entity.Trade;
import org.bank.tradingservice.entity.TradeStatus;
import org.bank.tradingservice.exception.InvalidTradeException;
import org.bank.tradingservice.exception.TradeNotFoundException;
import org.bank.tradingservice.execution.TradeExecutionOrchestrator;
import org.bank.tradingservice.mapper.TradeMapper;
import org.bank.tradingservice.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeService {


    private final TradeMapper tradeMapper;
    private final TradeExecutionOrchestrator orchestrator;

    @Transactional
    public TradeResponse createTrade(String userId, CreateTradeRequest request) {

        Trade trade = orchestrator.execute(userId, request);
        return tradeMapper.toResponse(trade);

    }

    public TradeResponse getTrade(UUID tradeId) {
        Trade trade = tradeRepository.findById(tradeId)
                .orElseThrow(
                        () -> new TradeNotFoundException("trade not found")
                );
        return tradeMapper.toResponse(trade);
    }

    public List<TradeResponse> getMyTrades(String userId) {
        return tradeRepository.findByUserId(userId)
                .stream()
                .map(tradeMapper::toResponse)
                .toList();
    }
}
