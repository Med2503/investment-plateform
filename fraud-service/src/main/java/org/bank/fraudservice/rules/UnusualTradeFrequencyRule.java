
package org.bank.fraudservice.rules;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudReason;
import org.bank.fraudservice.repository.FraudAlertRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UnusualTradeFrequencyRule implements FraudRule {

    private final FraudAlertRepository fraudAlertRepository;

    @Value("${fraud.rules.frequency-window-minutes:5}")
    private long windowMinutes;

    @Value("${fraud.rules.max-trades-in-window:10}")
    private long maxTradesInWindow;

    @Override
    public Optional<FraudReason> evaluate(FraudContext context) {
        Instant since = Instant.now().minus(windowMinutes, ChronoUnit.MINUTES);

        long recentAlerts = fraudAlertRepository.countRecentAlertsByUserId(
                context.trade().userId(),
                since
        );

        if (recentAlerts >= maxTradesInWindow) {
            return Optional.of(FraudReason.UNUSUAL_TRADE_FREQUENCY);
        }

        return Optional.empty();
    }

    @Override
    public int score() {
        return 40;
    }
}
