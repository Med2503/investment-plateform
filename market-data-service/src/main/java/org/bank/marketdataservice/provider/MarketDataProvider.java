package org.bank.marketdataservice.provider;

import java.math.BigDecimal;

public interface MarketDataProvider {
    BigDecimal getCurrentPrice(String symbol);
}
