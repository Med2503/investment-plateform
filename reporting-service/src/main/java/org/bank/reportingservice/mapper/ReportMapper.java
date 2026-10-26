
package org.bank.reportingservice.mapper;

import org.bank.reportingservice.dto.ReportResponse;
import org.bank.reportingservice.dto.ReportSummaryResponse;
import org.bank.reportingservice.entity.ReportEntity;

import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public ReportResponse toResponse(ReportEntity entity) {
        return new ReportResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getReportType(),
                entity.getFormat(),
                entity.getStatus(),
                entity.getPeriodStart(),
                entity.getPeriodEnd(),
                entity.getFileLocation(),
                entity.getErrorMessage(),
                entity.getCreatedAt(),
                entity.getStartedAt(),
                entity.getCompletedAt()
        );
    }

    public ReportSummaryResponse toSummaryResponse(ReportEntity entity) {
        return new ReportSummaryResponse(
                entity.getId(),
                entity.getReportType(),
                entity.getFormat(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }
}
