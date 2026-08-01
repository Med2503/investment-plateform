package org.bank.riskservice.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties
public class RiskProperties {

    private BigDecimal maxTradeAmount;
    private List<String> supportedSymbols = new ArrayList<>();
    private List<String> blackListedSymbols = new ArrayList<>();


    private Market market = new Market();


    @Getter
    @Setter
    public static class Market {
        private LocalTime open;
        private LocalTime close;
    }
}
