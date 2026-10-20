package org.bank.fraudservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.entity.FraudAlertEntity;
import org.bank.fraudservice.mapper.FraudAlertMapper;
import org.bank.fraudservice.model.FraudAnalysisResult;
import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.repository.FraudAlertRepository;
import org.bank.sharedevents.event.trade.TradeExecutedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudDetectionEngine detectionEngine;
    private final FraudAlertMapper alertMapper;
    private final FraudAlertRepository alertRepository;

    @Transactional
    public FraudAnalysisResult analyze(TradeExecutedEvent trade) {
        validate(trade);

        var existing = alertRepository.findByTradeId(trade.tradeId());

        if (existing.isPresent()) {
            FraudAlertEntity alert = existing.get();

            return new FraudAnalysisResult(
                    org.bank.fraudservice.model.FraudStatus.valueOf(
                            alert.getFraudStatus()
                    ),
                    alert.getTransactionAmount(),
                    alert.getRiskScore(),
                    parseReasons(alert.getReasons())
            );
        }

        BigDecimal amount = trade.quantity()
                .multiply(trade.executedPrice());

        FraudContext context = new FraudContext(trade, amount);

        FraudAnalysisResult result = detectionEngine.analyze(context);

        alertRepository.save(alertMapper.toEntity(context, result));

        return result;
    }

    private void validate(TradeExecutedEvent trade) {
        Objects.requireNonNull(trade, "trade must not be null");
        Objects.requireNonNull(trade.tradeId(), "tradeId must not be null");
        Objects.requireNonNull(trade.userId(), "userId must not be null");
        Objects.requireNonNull(trade.quantity(), "quantity must not be null");
        Objects.requireNonNull(
                trade.executedPrice(),
                "executedPrice must not be null"
        );

        if (trade.quantity().signum() <= 0
                || trade.executedPrice().signum() < 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive and executed price non-negative"
            );
        }
    }

    private java.util.List<org.bank.fraudservice.model.FraudReason>
    parseReasons(String reasons) {
        if (reasons == null || reasons.isBlank()) {
            return java.util.List.of();
        }

        return java.util.Arrays.stream(reasons.split(","))
                .map(String::trim)
                .map(org.bank.fraudservice.model.FraudReason::valueOf)
                .toList();
    }
}