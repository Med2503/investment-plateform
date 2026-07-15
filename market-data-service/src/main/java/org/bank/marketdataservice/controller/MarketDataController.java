package org.bank.marketdataservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bank.marketdataservice.dto.request.CreateMarketAssetRequest;
import org.bank.marketdataservice.dto.request.UpdateMarketPriceRequest;
import org.bank.marketdataservice.dto.response.MarketAssetResponse;
import org.bank.marketdataservice.service.MarketDataService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/market-data")
@RequiredArgsConstructor
public class MarketDataController {
    private final MarketDataService marketDataService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarketAssetResponse createAsset(
            @Valid @RequestBody CreateMarketAssetRequest request
    ) {
        return marketDataService.createAsset(request);
    }


    @GetMapping("/{symbol}")
    public MarketAssetResponse getAsset(@PathVariable String symbol) {
        return marketDataService.getAsset(symbol);
    }

    @GetMapping
    public List<MarketAssetResponse> getAllAssets() {
        return marketDataService.getAllAssets();
    }


    @PutMapping("/{symbol}/price")
    public MarketAssetResponse updatePrice(
            @PathVariable String symbol,
            @Valid @RequestBody UpdateMarketPriceRequest request
    ) {
        return marketDataService.updatePrice(
                symbol,
                request
        );
    }

    @DeleteMapping("/{symbol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAsset(@PathVariable String symbol) {
        marketDataService.deleteAsset(symbol);
    }
}
