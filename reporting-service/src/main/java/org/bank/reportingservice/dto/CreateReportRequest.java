
package org.bank.reportingservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;

import org.bank.reportingservice.model.ReportFormat;
import org.bank.reportingservice.model.ReportType;

import java.time.Instant;

public record CreateReportRequest(
        @NotNull ReportType reportType,
        @NotNull ReportFormat format,
        @NotNull Instant periodStart,
        @NotNull Instant periodEnd
) {
    @AssertTrue(message = "periodStart must be before periodEnd")
    public boolean isPeriodValid() {
        return periodStart != null
                && periodEnd != null
                && periodStart.isBefore(periodEnd);
    }
}
