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
        validateLeaseDuration();

        Instant now = Instant.now(clock);
        UUID token = UUID.randomUUID();
        Instant leaseUntil = now.plus(
                Duration.ofSeconds(leaseDurationSeconds)
        );

        int updatedRows = reportRepository.claimOrRecoverReport(
                reportId,
                ReportStatus.PENDING,
                ReportStatus.PROCESSING,
                now,
                leaseUntil,
                token
        );

        return updatedRows == 1
                ? Optional.of(token)
                : Optional.empty();
    }

    @Transactional
    public boolean renew(UUID reportId, UUID token) {
        validateLeaseDuration();

        Instant now = Instant.now(clock);
        Instant leaseUntil = now.plus(
                Duration.ofSeconds(leaseDurationSeconds)
        );

        int updatedRows = reportRepository.renewLease(
                reportId,
                ReportStatus.PROCESSING,
                token,
                now,
                leaseUntil
        );

        return updatedRows == 1;
    }

    private void validateLeaseDuration() {
        if (leaseDurationSeconds <= 0) {
            throw new IllegalStateException(
                    "reporting.processing.lease-duration-seconds "
                            + "must be greater than zero"
            );
        }
    }
}