package org.bank.riskservice.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@Getter
@Setter
@ConfigurationProperties(prefix = "risk.volatility")
public class VolatilityProperties {
    private BigDecimal lowLimit = new BigDecimal("20");
    private BigDecimal mediumLimit = new BigDecimal("50");
}
