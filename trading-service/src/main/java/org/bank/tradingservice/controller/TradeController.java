package org.bank.tradingservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.tradingservice.dto.request.CreateTradeRequest;
import org.bank.tradingservice.dto.response.TradeResponse;
import org.bank.tradingservice.service.TradeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TradeResponse createTrade(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateTradeRequest request
    ) {
        return service.createTrade(userId, request);
    }

    @GetMapping("/{tradeId}")
    public TradeResponse getTrade(@PathVariable UUID tradeId) {
        return service.getTrade(tradeId);
    }


    @GetMapping("/me")
    public List<TradeResponse> getMyTrades(@RequestHeader("X-User-Id") String userId) {
        return service.getMyTrades(userId);
    }

}
