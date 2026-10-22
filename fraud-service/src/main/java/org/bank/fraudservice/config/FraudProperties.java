
package org.bank.fraudservice.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@ConfigurationProperties(prefix = "fraud")
public record FraudProperties(
        @Valid @NotNull Rules rules,
        @Valid @NotNull Thresholds thresholds
) {

    public FraudProperties {
        if (rules == null) {
            rules = new Rules(
                    new BigDecimal("100000"),
                    5,
                    10,
                    Set.of()
            );
        }

        if (thresholds == null) {
            thresholds = new Thresholds(40, 80);
        }
    }

    public record Rules(
            @NotNull @DecimalMin("0.0") BigDecimal highAmountThreshold,
            @Min(1) long frequencyWindowMinutes,
            @Min(1) long maxTradesInWindow,
            Set<String> suspiciousSymbols
    ) {
        public Rules {
            highAmountThreshold = highAmountThreshold == null
                    ? new BigDecimal("100000")
                    : highAmountThreshold;

            suspiciousSymbols = suspiciousSymbols == null
                    ? Set.of()
                    : suspiciousSymbols.stream()
                    .filter(symbol -> symbol != null && !symbol.isBlank())
                    .map(symbol -> symbol.trim().toUpperCase(Locale.ROOT))
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    public record Thresholds(
            @Min(0) int review,
            @Min(1) int reject
    ) {
    }
}
