package org.bank.fraudservice.rules;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.config.FraudProperties;
import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudReason;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class HighTransactionAmountRule implements FraudRule {

    private final FraudProperties properties;

    @Override
    public Optional<FraudReason> evaluate(FraudContext context) {
        if (context.transactionAmount().compareTo(
                properties.rules().highAmountThreshold()
        ) > 0) {
            return Optional.of(FraudReason.HIGH_TRANSACTION_AMOUNT);
        }

        return Optional.empty();
    }

    @Override
    public int score() {
        return 40;
    }
}