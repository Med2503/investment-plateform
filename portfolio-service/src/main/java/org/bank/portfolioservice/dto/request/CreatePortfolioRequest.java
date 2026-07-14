package org.bank.portfolioservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePortfolioRequest(
        @NotBlank
        String name
) {
}
