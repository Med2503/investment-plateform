package org.bank.riskservice.observability;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import org.springframework.stereotype.Component;

@Component
public class TraceContextProvider {

    public String currentTraceparent() {
        SpanContext context = Span.current().getSpanContext();

        if (!context.isValid()) {
            return null;
        }

        return "00-"
                + context.getTraceId()
                + "-"
                + context.getSpanId()
                + "-"
                + String.format(
                "%02x",
                context.getTraceFlags().asByte() & 0xff
        );
    }
}
