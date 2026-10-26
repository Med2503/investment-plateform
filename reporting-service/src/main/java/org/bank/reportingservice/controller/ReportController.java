
package org.bank.reportingservice.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.bank.reportingservice.dto.CreateReportRequest;
import org.bank.reportingservice.dto.ReportResponse;
import org.bank.reportingservice.dto.ReportSummaryResponse;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.model.ReportType;
import org.bank.reportingservice.service.ReportService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestHeader("X-Authenticated-User") String userId,
            @Valid @RequestBody CreateReportRequest request
    ) {
        ReportResponse response = reportService.createReport(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponse> getReport(
            @PathVariable UUID reportId,
            @RequestHeader("X-Authenticated-User") String userId
    ) {
        return ResponseEntity.ok(
                reportService.getReport(reportId, userId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<ReportSummaryResponse>> getReports(
            @RequestHeader("X-Authenticated-User") String userId,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) ReportType reportType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page index must not be negative"
            );
        }

        if (size < 1) {
            throw new IllegalArgumentException(
                    "Page size must be greater than zero"
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                Math.min(size, MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return ResponseEntity.ok(
                reportService.getReports(
                        userId,
                        status,
                        reportType,
                        pageable
                )
        );
    }
}
