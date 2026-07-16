package org.bank.marketdataservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.marketdataservice.dto.request.CreateMarketAssetRequest;
import org.bank.marketdataservice.dto.request.UpdateMarketPriceRequest;
import org.bank.marketdataservice.dto.response.MarketAssetResponse;
import org.bank.marketdataservice.entity.MarketAsset;
import org.bank.marketdataservice.exception.InvalidMarketPriceException;
import org.bank.marketdataservice.exception.MarketAssetAlreadyExistsException;
import org.bank.marketdataservice.exception.MarketAssetNotFoundException;
import org.bank.marketdataservice.mapper.MarketAssetMapper;
import org.bank.marketdataservice.provider.MarketDataProvider;
import org.bank.marketdataservice.repository.MarketAssetRepository;
import org.bank.sharedevents.event.MarketPriceUpdatedEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final MarketAssetRepository marketAssetRepository;
    private final MarketAssetMapper mapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MarketDataProvider marketDataProvider;


    @Transactional
    public MarketAssetResponse createAsset(CreateMarketAssetRequest request) {
        if (marketAssetRepository.existsBySymbol(request.symbol())) {
            throw new MarketAssetAlreadyExistsException("Market asset exists");
        }
        if (request.currentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMarketPriceException("Price must be greater than 0");
        }

        MarketAsset asset = MarketAsset.builder()
                .symbol(request.symbol().toUpperCase())
                .name(request.name())
                .currentPrice(request.currentPrice())
                .currency(request.currency().toUpperCase())
                .lastUpdated(Instant.now())
                .build();

        return mapper.toResponse(
                marketAssetRepository.save(asset)
        );
    }

    @Cacheable(
            value = "market-assets",
            key = "#symbol"
    )
    public MarketAssetResponse getAsset(
            String symbol
    ) {

        MarketAsset asset =
                marketAssetRepository.findBySymbol(symbol.toUpperCase())
                        .orElseThrow(
                                () -> new MarketAssetNotFoundException(
                                        "Market asset not found"
                                )
                        );

        return mapper.toResponse(asset);

    }


    public List<MarketAssetResponse> getAllAssets() {
        return marketAssetRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }


    @CachePut(
            value = "market-assets",
            key = "#symbol"
    )
    @Transactional
    public MarketAssetResponse updatePrice(
            String symbol,
            UpdateMarketPriceRequest request
    ) {

        if (request.currentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidMarketPriceException(
                    "Price must be greater than zero"
            );
        }


        MarketAsset asset =
                marketAssetRepository.findBySymbol(symbol.toUpperCase())
                        .orElseThrow(
                                () -> new MarketAssetNotFoundException(
                                        "Market asset not found"
                                )
                        );

        BigDecimal oldPrice = asset.getCurrentPrice();


        asset.setCurrentPrice(request.currentPrice());
        asset.setLastUpdated(Instant.now());
        MarketAsset saved = marketAssetRepository.save(asset);

        kafkaTemplate.send(
                "market-price-updated",
                new MarketPriceUpdatedEvent(
                        saved.getSymbol(),
                        oldPrice,
                        saved.getCurrentPrice(),
                        saved.getCurrency(),
                        saved.getLastUpdated()
                )

        );
        return mapper.toResponse(saved);


    }

    @CacheEvict(
            value = "market-assets",
            key = "#symbol"
    )
    @Transactional
    public void deleteAsset(
            String symbol
    ) {

        MarketAsset asset =
                marketAssetRepository.findBySymbol(symbol.toUpperCase())
                        .orElseThrow(
                                () -> new MarketAssetNotFoundException(
                                        "Market asset not found"
                                )
                        );

        marketAssetRepository.delete(asset);

    }

    @Transactional
    public MarketAssetResponse refreshPrice(String symbol) {
        MarketAsset asset = marketAssetRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(
                        () -> new MarketAssetNotFoundException("not found!")
                );
        BigDecimal oldPrice = asset.getCurrentPrice();
        BigDecimal newPrice = marketDataProvider.getCurrentPrice(symbol);

        if(newPrice.compareTo(BigDecimal.ZERO)<=0){

            throw new InvalidMarketPriceException(
                    "Invalid provider price"
            );
        }



        asset.setCurrentPrice(
                newPrice
        );


        asset.setLastUpdated(
                Instant.now()
        );



        MarketAsset saved =
                marketAssetRepository.save(asset);

        kafkaTemplate.send(
                "market-price-updated",
                new MarketPriceUpdatedEvent(
                        saved.getSymbol(),
                        oldPrice,
                        saved.getCurrentPrice(),
                        saved.getCurrency(),
                        saved.getLastUpdated()
                )
        );



        return mapper.toResponse(saved);
    }

}
