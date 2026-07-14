package org.bank.portfolioservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.portfolioservice.dto.request.AddAssetRequest;
import org.bank.portfolioservice.dto.request.CreatePortfolioRequest;
import org.bank.portfolioservice.dto.response.PortfolioAssetResponse;
import org.bank.portfolioservice.dto.response.PortfolioResponse;
import org.bank.portfolioservice.entity.Portfolio;
import org.bank.portfolioservice.entity.PortfolioAsset;
import org.bank.portfolioservice.exception.*;
import org.bank.portfolioservice.mapper.PortfolioAssetMapper;
import org.bank.portfolioservice.mapper.PortfolioMapper;
import org.bank.portfolioservice.repository.PortfolioAssetRepository;
import org.bank.portfolioservice.repository.PortfolioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioAssetRepository portfolioAssetRepository;
    private final PortfolioMapper portfolioMapper;
    private final PortfolioAssetMapper portfolioAssetMapper;


    @Transactional
    public PortfolioResponse createPortfolio(String userId, CreatePortfolioRequest request) {

        if (portfolioRepository.findByUserId(userId).isPresent()) {
            throw new PortfolioAlreadyExistsException("User already has a portfolio");
        }


        Portfolio portfolio = Portfolio.builder()
                .userId(userId)
                .name(request.name())
                .totalValue(BigDecimal.ZERO)
                .createdAt(Instant.now())
                .build();

        return portfolioMapper.toResponse(
                portfolioRepository.save(portfolio)
        );
    }

    public PortfolioResponse getMyPortfolio(String userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId)
                .orElseThrow(
                        () -> new PortfolioNotFoundException("Portfolio for this userId not found")
                );
        return portfolioMapper.toResponse(portfolio);
    }


    @Transactional
    public PortfolioAssetResponse addAsset(String userId, AddAssetRequest request) {

        Portfolio portfolio = portfolioRepository.findByUserId(userId).orElseThrow(
                () -> new PortfolioAssetNotFoundException("Portfolio not found!")
        );

        if (request.quantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidQuantityException("quantity must be greater than zero");
        }
        if (request.averagePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidQuantityException("average proce must be greater than 0");
        }

        portfolioAssetRepository.findByPortfolioIdAndSymbol(
                portfolio.getId(),
                request.symbol().toUpperCase()
        ).ifPresent(
                asset -> {
                    throw new AssetAlreadyExistsException("Assets already exists in portfolio!!");
                });

        PortfolioAsset asset = PortfolioAsset.builder()
                .portfolio(portfolio)
                .symbol(request.symbol().toUpperCase())
                .quantity(request.quantity())
                .averagePrice(request.averagePrice())
                .createdAt(Instant.now())
                .build();

        return portfolioAssetMapper.toResponse(
                portfolioAssetRepository.save(asset)
        );

    }

    public List<PortfolioAssetResponse> getAssets(String userId) {
        Portfolio portfolio = portfolioRepository.findByUserId(userId).orElseThrow();

        return portfolioAssetRepository.findByPortfolioId(portfolio.getId())
                .stream()
                .map(portfolioAssetMapper::toResponse)
                .toList();
    }

}
