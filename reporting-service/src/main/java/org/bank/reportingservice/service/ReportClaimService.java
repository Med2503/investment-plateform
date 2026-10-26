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
public class ReportClaimService {

    private final ReportRepository reportRepository;
    private final Clock clock;

    @Transactional
    public boolean claim(UUID reportId) {
        Instant now = Instant.now(clock);

        int updatedRows = reportRepository.claimPendingReport(
                reportId,
                ReportStatus.PENDING,
                ReportStatus.PROCESSING,
                now
        );

        return updatedRows == 1;
    }
}