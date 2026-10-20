package org.bank.fraudservice.mapper;

import org.bank.fraudservice.entity.FraudAlertEntity;
import org.bank.fraudservice.model.FraudAnalysisResult;
import org.bank.fraudservice.model.FraudContext;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FraudAlertMapper {

    public FraudAlertEntity toEntity(
            FraudContext context,
            FraudAnalysisResult result
    ) {
        String reasons = result.reasons()
                .stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        return FraudAlertEntity.builder()
                .tradeId(context.trade().tradeId())
                .userId(context.trade().userId())
                .symbol(context.trade().symbol())
                .transactionAmount(result.transactionAmount())
                .riskScore(result.riskScore())
                .fraudStatus(result.status().name())
                .reasons(reasons)
                .build();
    }
}