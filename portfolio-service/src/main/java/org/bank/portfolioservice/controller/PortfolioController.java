package org.bank.portfolioservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.portfolioservice.dto.CreatePortfolioRequest;
import org.bank.portfolioservice.dto.PortfolioResponse;
import org.bank.portfolioservice.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

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

}
