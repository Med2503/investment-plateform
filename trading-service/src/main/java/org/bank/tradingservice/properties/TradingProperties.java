package org.bank.tradingservice.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "trading")
public class TradingProperties {

    private BigDecimal maximumDailyExposure;
}
