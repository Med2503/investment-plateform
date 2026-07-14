package org.bank.portfolioservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.portfolioservice.dto.CreatePortfolioRequest;
import org.bank.portfolioservice.dto.PortfolioResponse;
import org.bank.portfolioservice.entity.Portfolio;
import org.bank.portfolioservice.mapper.PortfolioMapper;
import org.bank.portfolioservice.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository repository;
    private final PortfolioMapper mapper;


    @Transactional
    public PortfolioResponse createPortfolio(String userId, CreatePortfolioRequest request) {

        if (repository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("User already has portfolio");
        }


        Portfolio portfolio = Portfolio.builder()
                .userId(userId)
                .name(request.name())
                .totalValue(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .build();

        return mapper.toResponse(
                repository.save(portfolio)
        );
    }

    public PortfolioResponse getMyPortfolio(String userId) {
        Portfolio portfolio = repository.findByUserId(userId)
                .orElseThrow(
                        () -> new RuntimeException("Portfolio not found")
                );
        return mapper.toResponse(portfolio);
    }
}
