package org.bank.tradingservice.client;


import org.bank.tradingservice.dto.request.SellAssetRequest;
import org.bank.tradingservice.dto.response.PortfolioAssetResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "portfolio-client")
public interface PortfolioClient {

    @GetMapping("/api/v1/portfolios/me/assets/{symbol}")
    PortfolioAssetResponse getAsset(@RequestHeader("X-User-Id") String userId,
                                    @PathVariable String symbol);


    @PostMapping("/api/v1/portfolios/me/assets/sell")
    void sellAsset(@RequestHeader("X-User-Id") String userId,
                   @RequestBody SellAssetRequest request);


}
