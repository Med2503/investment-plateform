package org.bank.portfolioservice.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PortfolioResponse(
        UUID id,
        String userId,
        String name,
        BigDecimal totalValue,
        Instant createdAt
) {
}
