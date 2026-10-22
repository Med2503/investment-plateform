package org.bank.fraudservice.rules;

import lombok.RequiredArgsConstructor;
import org.bank.fraudservice.config.FraudProperties;
import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudReason;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SuspiciousAssetRule implements FraudRule {

    private final FraudProperties properties;

    @Override
    public Optional<FraudReason> evaluate(FraudContext context) {
        String symbol = context.trade().symbol();

        if (symbol == null || symbol.isBlank()) {
            return Optional.empty();
        }

        boolean suspicious = properties.rules()
                .suspiciousSymbols()
                .contains(symbol.trim().toUpperCase(Locale.ROOT));

        return suspicious
                ? Optional.of(FraudReason.SUSPICIOUS_ASSET)
                : Optional.empty();
    }

    @Override
    public int score() {
        return 40;
    }
}