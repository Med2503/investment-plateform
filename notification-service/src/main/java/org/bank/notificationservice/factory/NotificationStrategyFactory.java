package org.bank.notificationservice.factory;

import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.entity.NotificationChannel;
import org.bank.notificationservice.strategy.NotificationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationStrategyFactory {

    private final List<NotificationStrategy> strategies;

    public NotificationStrategy getStrategy(NotificationChannel channel) {
        return strategies.stream()
                .filter(strategy -> strategy.supports() == channel)
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException("Not supported channel " + channel)
                );
    }

}
