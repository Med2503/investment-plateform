
package org.bank.reportingservice.dto;

import org.bank.reportingservice.model.ReportFormat;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.model.ReportType;

import java.time.Instant;
import java.util.UUID;

public record ReportSummaryResponse(
        UUID id,
        ReportType reportType,
        ReportFormat format,
        ReportStatus status,
        Instant createdAt,
        Instant completedAt
) {
}
