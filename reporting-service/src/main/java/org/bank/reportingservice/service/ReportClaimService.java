package org.bank.reportingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportClaimService {

    private final ReportLeaseService reportLeaseService;

    public Optional<UUID> claim(UUID reportId) {
        return reportLeaseService.claimOrRecover(reportId);
    }
}