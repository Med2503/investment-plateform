
package org.bank.fraudservice.mapper;

import org.bank.fraudservice.entity.FraudAlertEntity;
import org.bank.fraudservice.model.FraudAlertResponse;
import org.bank.fraudservice.model.FraudReason;
import org.bank.fraudservice.model.FraudStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FraudAlertResponseMapper {

    public FraudAlertResponse toResponse(FraudAlertEntity entity) {
        List<FraudReason> reasons =
                entity.getReasons() == null || entity.getReasons().isBlank()
                        ? List.of()
                        : Arrays.stream(entity.getReasons().split(","))
                        .map(String::trim)
                        .map(FraudReason::valueOf)
                        .toList();

        return new FraudAlertResponse(
                entity.getId(),
                entity.getTradeId(),
                entity.getUserId(),
                entity.getSymbol(),
                entity.getTransactionAmount(),
                entity.getRiskScore(),
                FraudStatus.valueOf(entity.getFraudStatus()),
                reasons,
                entity.getCreatedAt()
        );
    }
}
