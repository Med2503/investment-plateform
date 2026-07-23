package org.bank.portfolioservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.portfolioservice.dto.request.AddAssetRequest;
import org.bank.portfolioservice.dto.request.CreatePortfolioRequest;
import org.bank.portfolioservice.dto.request.SellAssetRequest;
import org.bank.portfolioservice.dto.response.PortfolioAssetResponse;
import org.bank.portfolioservice.dto.response.PortfolioResponse;
import org.bank.portfolioservice.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService service;

    @PostMapping
    public PortfolioResponse create(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreatePortfolioRequest request
    ) {
        return service.createPortfolio(userId, request);
    }

    @GetMapping("/me")
    public PortfolioResponse getMyPortfolio(
            @RequestHeader("X-User-Id") String userId

    ) {
        return service.getMyPortfolio(userId);
    }

    @PostMapping("/assets")
    public PortfolioAssetResponse addAsset(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody AddAssetRequest request
    ) {
        return service.addAsset(userId, request);
    }

    @GetMapping("/assets")
    public List<PortfolioAssetResponse> getAssets(@RequestHeader("X-User-Id") String userId) {
        return service.getAssets(userId);
    }

    @PostMapping("/me/assets/sell")
    public void sellAsset(@RequestHeader("X-User-Id") String userId, @Valid @RequestBody SellAssetRequest request) {
        service.sellAsset(userId, request);
    }

}
