package org.bank.fraudservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudReason;
import org.bank.fraudservice.model.FraudStatus;
import org.bank.fraudservice.model.FraudAnalysisResult;
import org.bank.fraudservice.rules.FraudRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FraudDetectionEngine {

    private final List<FraudRule> rules;

    @Value("${fraud.thresholds.review:40}")
    private int reviewThreshold;

    @Value("${fraud.thresholds.reject:80}")
    private int rejectThreshold;

    public FraudAnalysisResult analyze(FraudContext context) {
        List<FraudReason> reasons = new ArrayList<>();
        int score = 0;

        for (FraudRule rule : rules) {
            var result = rule.evaluate(context);

            if (result.isPresent()) {
                reasons.add(result.get());
                score += rule.score();
            }
        }

        score = Math.min(score, 100);

        FraudStatus status;

        if (score >= rejectThreshold) {
            status = FraudStatus.REJECTED;
        } else if (score >= reviewThreshold) {
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