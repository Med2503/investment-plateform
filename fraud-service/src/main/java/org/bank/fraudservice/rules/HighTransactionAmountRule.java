package org.bank.fraudservice.rules;

import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudReason;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class HighTransactionAmountRule implements FraudRule {

    private final BigDecimal threshold;

    public HighTransactionAmountRule(
            @Value("${fraud.rules.high-amount-threshold:100000}")
            BigDecimal threshold
    ) {
        this.threshold = threshold;
    }

    @Override
    public Optional<FraudReason> evaluate(FraudContext context) {
        if (context.transactionAmount().compareTo(threshold) > 0) {
            return Optional.of(FraudReason.HIGH_TRANSACTION_AMOUNT);
        }

        return Optional.empty();
    }

    @Override
    public int score() {
        return 40;
    }
}