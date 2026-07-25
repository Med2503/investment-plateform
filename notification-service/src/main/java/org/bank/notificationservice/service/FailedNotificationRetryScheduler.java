package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bank.notificationservice.entity.FailedNotification;
import org.bank.notificationservice.entity.FailedNotificationStatus;
import org.bank.notificationservice.repository.FailedNotificationRepository;
import org.bank.notificationservice.service.NotificationRetryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FailedNotificationRetryScheduler {

    private final FailedNotificationRepository repository;

    private final NotificationRetryService retryService;

    @Scheduled(fixedDelay = 300000)
    public void retryFailedNotifications() {

        List<FailedNotification> notifications =
                repository.findByStatusAndNextRetryAtBefore(
                        FailedNotificationStatus.PENDING,
                        Instant.now()
                );

        for (FailedNotification notification : notifications) {

            try {

                /*
                 * reutiliser notification-service pour re construire et envoyer la notification
                 */

                retryService.success(notification);

            } catch (Exception ex) {

                retryService.failure(
                        notification,
                        ex
                );

            }

        }

    }

}
