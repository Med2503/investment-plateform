
package org.bank.fraudservice.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.bank.fraudservice.model.FraudStatus;
import org.springframework.stereotype.Component;

@Component
public class FraudMetrics {

    private final MeterRegistry meterRegistry;

    public FraudMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordAnalysis(FraudStatus status) {
        meterRegistry.counter(
                "fraud.analysis.total",
                "status",
                status.name()
        ).increment();
    }

    public void recordError() {
        meterRegistry.counter("fraud.analysis.errors").increment();
    }

    public void recordDuplicateEvent() {
        meterRegistry.counter("fraud.analysis.duplicate.events").increment();
    }
    public void recordDltEvent() {
        meterRegistry.counter("fraud.dlt.events").increment();
    }
}
