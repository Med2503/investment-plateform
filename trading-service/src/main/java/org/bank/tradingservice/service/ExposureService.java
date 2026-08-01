package org.bank.tradingservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.tradingservice.dto.response.DailyExposureResponse;
import org.bank.tradingservice.entity.TradeStatus;
import org.bank.tradingservice.properties.TradingProperties;
import org.bank.tradingservice.repository.TradeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ExposureService {

    private final TradeRepository tradeRepository;
    private final TradingProperties tradingProperties;

    public DailyExposureResponse getExposure(String userId) {
        BigDecimal todayExposure = tradeRepository.calculateTodayExposure(userId, TradeStatus.EXECUTED, LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC));

        BigDecimal maximumExposure = tradingProperties.getMaximumDailyExposure();
        BigDecimal remainingExposure = maximumExposure.subtract(todayExposure);

        if (remainingExposure.signum() < 0) {
            remainingExposure = BigDecimal.ZERO;
        }

        return new DailyExposureResponse(
                todayExposure,
                remainingExposure,
                maximumExposure
        );

    }
}
