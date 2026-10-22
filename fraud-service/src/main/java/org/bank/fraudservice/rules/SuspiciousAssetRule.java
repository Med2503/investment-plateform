
package org.bank.fraudservice.rules;

import org.bank.fraudservice.model.FraudContext;
import org.bank.fraudservice.model.FraudReason;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SuspiciousAssetRule implements FraudRule {

    private final Set<String> suspiciousSymbols;

    public SuspiciousAssetRule(
            @Value("${fraud.rules.suspicious-symbols:}") Set<String> suspiciousSymbols
    ) {
        this.suspiciousSymbols = suspiciousSymbols.stream()
                .map(symbol -> symbol.trim().toUpperCase(Locale.ROOT))
                .filter(symbol -> !symbol.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<FraudReason> evaluate(FraudContext context) {
        String symbol = context.trade().symbol();

        if (symbol == null || symbol.isBlank()) {
            return Optional.empty();
        }

        if (suspiciousSymbols.contains(symbol.trim().toUpperCase(Locale.ROOT))) {
            return Optional.of(FraudReason.SUSPICIOUS_ASSET);
        }

        return Optional.empty();
    }

    @Override
    public int score() {
        return 40;
    }
}
