package org.bank.fraudservice.rules;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.config.FraudProperties;
import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudReason;
import org.bank.fraudservice.repository.FraudAlertRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UnusualTradeFrequencyRule implements FraudRule {

    private final FraudAlertRepository fraudAlertRepository;
    private final FraudProperties properties;

    @Override
    public Optional<FraudReason> evaluate(FraudContext context) {
        var rules = properties.rules();

        Instant since = Instant.now().minus(
                rules.frequencyWindowMinutes(),
                ChronoUnit.MINUTES
        );

        long recentAlerts = fraudAlertRepository.countRecentAlertsByUserId(
                context.trade().userId(),
                since
        );

        if (recentAlerts >= rules.maxTradesInWindow()) {
            return Optional.of(FraudReason.UNUSUAL_TRADE_FREQUENCY);
        }

        return Optional.empty();
    }

    @Override
    public int score() {
        return 40;
    }
}