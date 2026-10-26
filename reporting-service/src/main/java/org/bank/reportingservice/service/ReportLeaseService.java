package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.bank.reportingservice.model.ReportStatus;
import org.bank.reportingservice.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportLeaseService {

    private final ReportRepository reportRepository;
    private final Clock clock;

    @Value("${reporting.processing.lease-duration-seconds:300}")
    private long leaseDurationSeconds;

    @Transactional
    public Optional<UUID> claimOrRecover(UUID reportId) {
        Instant now = Instant.now(clock);
        UUID token = UUID.randomUUID();

        int updated = reportRepository.claimOrRecoverReport(
                reportId,
                ReportStatus.PENDING,
                ReportStatus.PROCESSING,
                now,
                now.plus(Duration.ofSeconds(leaseDurationSeconds)),
                token
        );

        return updated == 1 ? Optional.of(token) : Optional.empty();
    }

    @Transactional
    public boolean renew(UUID reportId, UUID token) {
        Instant now = Instant.now(clock);

        int updated = reportRepository.renewLease(
                reportId,
                ReportStatus.PROCESSING,
                token,
                now,
                now.plus(Duration.ofSeconds(leaseDurationSeconds))
        );

        return updated == 1;
    }
}