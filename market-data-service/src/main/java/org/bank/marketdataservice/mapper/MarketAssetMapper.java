package org.bank.marketdataservice.mapper;

import org.bank.marketdataservice.dto.response.MarketAssetResponse;
import org.bank.marketdataservice.entity.MarketAsset;
import org.springframework.stereotype.Component;

@Component
public class MarketAssetMapper {

    public MarketAssetResponse toResponse(MarketAsset asset) {

        return new MarketAssetResponse(
                asset.getId(),
                asset.getSymbol(),
                asset.getName(),
                asset.getCurrentPrice(),
                asset.getCurrency(),
                asset.getLastUpdated()
        );

    }

}
