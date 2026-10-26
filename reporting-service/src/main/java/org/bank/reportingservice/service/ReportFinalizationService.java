package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportFinalizationService {

    private static final int MAX_ERROR_LENGTH = 2000;

    private final ReportRepository reportRepository;
    private final Clock clock;

    @Transactional
    public boolean complete(
            UUID reportId,
            UUID token,
            String fileLocation
    ) {
        Instant now = Instant.now(clock);

        return reportRepository.completeClaimedReport(
                reportId,
                ReportStatus.PROCESSING,
                ReportStatus.COMPLETED,
                token,
                fileLocation,
                now
        ) == 1;
    }

    @Transactional
    public boolean fail(
            UUID reportId,
            UUID token,
            String errorMessage
    ) {
        Instant now = Instant.now(clock);
        String safeMessage = sanitize(errorMessage);

        return reportRepository.failClaimedReport(
                reportId,
                ReportStatus.PROCESSING,
                ReportStatus.FAILED,
                token,
                safeMessage,
                now
        ) == 1;
    }

    private String sanitize(String message) {
        if (message == null || message.isBlank()) {
            return "Report processing failed";
        }

        String safe = message.replaceAll("[\\r\\n\\t]", " ");

        return safe.length() > MAX_ERROR_LENGTH
                ? safe.substring(0, MAX_ERROR_LENGTH)
                : safe;
    }
}