package org.bank.fraudservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.config.FraudProperties;
import org.bank.fraudservice.model.FraudAnalysisResult;
import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudStatus;
import org.bank.fraudservice.rules.FraudRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FraudDetectionEngine {

    private final List<FraudRule> rules;
    private final FraudProperties properties;

    public FraudAnalysisResult analyze(FraudContext context) {
        List<org.bank.fraudservice.model.FraudReason> reasons =
                new ArrayList<>();

        int score = 0;

        for (FraudRule rule : rules) {
            var result = rule.evaluate(context);

            if (result.isPresent()) {
                reasons.add(result.get());
                score += rule.score();
            }
        }

        score = Math.min(score, 100);

        var thresholds = properties.thresholds();

        FraudStatus status;

        if (score >= thresholds.reject()) {
            status = FraudStatus.REJECTED;
        } else if (score >= thresholds.review()) {
            status = FraudStatus.REVIEW_REQUIRED;
        } else {
            status = FraudStatus.APPROVED;
        }

        return new FraudAnalysisResult(
                status,
                context.transactionAmount(),
                score,
                reasons
        );
    }
}