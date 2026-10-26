
package org.bank.reportingservice.dto;

import org.bank.reportingservice.model.ReportFormat;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.model.ReportType;

import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        String userId,
        ReportType reportType,
        ReportFormat format,
        ReportStatus status,
        Instant periodStart,
        Instant periodEnd,
        String fileLocation,
        String errorMessage,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt
) {
}
