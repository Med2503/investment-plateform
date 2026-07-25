package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.entity.FailedNotification;
import org.bank.notificationservice.entity.FailedNotificationStatus;
import org.bank.notificationservice.repository.FailedNotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationRetryService {

    private final FailedNotificationRepository repository;

    @Transactional
    public void success(FailedNotification notification) {

        notification.setStatus(
                FailedNotificationStatus.COMPLETED
        );

        notification.setCompletedAt(
                Instant.now()
        );

        repository.save(notification);

    }

    @Transactional
    public void failure(
            FailedNotification notification,
            Exception ex
    ) {

        notification.setRetryCount(
                notification.getRetryCount() + 1
        );

        notification.setLastRetryAt(
                Instant.now()
        );

        notification.setLastError(
                ex.getMessage()
        );

        if (notification.getRetryCount() >=
                notification.getMaxRetry()) {

            notification.setStatus(
                    FailedNotificationStatus.FAILED
            );

        } else {

            notification.setStatus(
                    FailedNotificationStatus.PENDING
            );

            notification.setNextRetryAt(
                    Instant.now().plusSeconds(300)
            );

        }

        repository.save(notification);

    }

}
