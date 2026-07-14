package org.bank.portfolioservice.mapper;


import org.bank.portfolioservice.dto.response.PortfolioResponse;
import org.bank.portfolioservice.entity.Portfolio;
import org.springframework.stereotype.Component;

@Component
public class PortfolioMapper {
    public PortfolioResponse toResponse(Portfolio portfolio) {
        return new PortfolioResponse(
                portfolio.getId(),
                portfolio.getUserId(),
                portfolio.getName(),
                portfolio.getTotalValue(),
                portfolio.getCreatedAt()
        );
    }
}
