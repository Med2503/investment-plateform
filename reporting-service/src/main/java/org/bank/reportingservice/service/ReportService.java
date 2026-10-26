
package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;

import org.bank.reportingservice.dto.CreateReportRequest;
import org.bank.reportingservice.dto.ReportResponse;
import org.bank.reportingservice.dto.ReportSummaryResponse;
import org.bank.reportingservice.entity.ReportEntity;
import org.bank.reportingservice.exception.InvalidReportRequestException;
import org.bank.reportingservice.exception.ReportNotFoundException;
import org.bank.reportingservice.mapper.ReportMapper;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.model.ReportType;
import org.bank.reportingservice.repository.ReportRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final Duration MAX_REPORT_PERIOD = Duration.ofDays(366);
    private static final Duration MAX_FUTURE_OFFSET = Duration.ofMinutes(5);

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
    private final Clock clock;

    @Transactional
    public ReportResponse createReport(
            String userId,
            CreateReportRequest request
    ) {
        validateUserId(userId);

        if (request == null) {
            throw new InvalidReportRequestException(
                    "Report request must not be null"
            );
        }

        validatePeriod(request.periodStart(), request.periodEnd());

        ReportEntity entity = new ReportEntity(
                userId,
                request.reportType(),
                request.format(),
                request.periodStart(),
                request.periodEnd()
        );

        ReportEntity saved = reportRepository.save(entity);
        return reportMapper.toResponse(saved);
    }

    public ReportResponse getReport(UUID reportId, String userId) {
        validateUserId(userId);

        ReportEntity entity = reportRepository
                .findByIdAndUserId(reportId, userId)
                .orElseThrow(() -> new ReportNotFoundException(reportId));

        return reportMapper.toResponse(entity);
    }

    public Page<ReportSummaryResponse> getReports(
            String userId,
            ReportStatus status,
            ReportType reportType,
            Pageable pageable
    ) {
        validateUserId(userId);

        Page<ReportEntity> reports;

        if (status != null && reportType != null) {
            // Use the dedicated combined filter added to the repository.
            reports = reportRepository
                    .findByUserIdAndStatusAndReportTypeOrderByCreatedAtDesc(
                            userId, status, reportType, pageable
                    );
        } else if (status != null) {
            reports = reportRepository
                    .findByUserIdAndStatusOrderByCreatedAtDesc(
                            userId, status, pageable
                    );
        } else if (reportType != null) {
            reports = reportRepository
                    .findByUserIdAndReportTypeOrderByCreatedAtDesc(
                            userId, reportType, pageable
                    );
        } else {
            reports = reportRepository
                    .findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        return reports.map(reportMapper::toSummaryResponse);
    }

    public long countPendingReports(String userId) {
        validateUserId(userId);
        return reportRepository.countByUserIdAndStatus(
                userId,
                ReportStatus.PENDING
        );
    }

    private void validatePeriod(Instant start, Instant end) {
        if (start == null || end == null) {
            throw new InvalidReportRequestException(
                    "Report period boundaries are required"
            );
        }

        if (!start.isBefore(end)) {
            throw new InvalidReportRequestException(
                    "Report period start must be before period end"
            );
        }

        if (Duration.between(start, end).compareTo(MAX_REPORT_PERIOD) > 0) {
            throw new InvalidReportRequestException(
                    "Report period must not exceed 366 days"
            );
        }

        Instant latestAllowedEnd = Instant.now(clock).plus(MAX_FUTURE_OFFSET);
        if (end.isAfter(latestAllowedEnd)) {
            throw new InvalidReportRequestException(
                    "Report period must not end in the future"
            );
        }
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new InvalidReportRequestException(
                    "User identifier must not be blank"
            );
        }
    }
}
