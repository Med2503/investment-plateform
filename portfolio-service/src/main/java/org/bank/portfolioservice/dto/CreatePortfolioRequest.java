package org.bank.portfolioservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePortfolioRequest(
        @NotBlank
        String name
) {
}
