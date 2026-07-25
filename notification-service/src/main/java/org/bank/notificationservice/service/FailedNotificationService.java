package org.bank.notificationservice.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.entity.FailedNotification;
import org.bank.notificationservice.repository.FailedNotificationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FailedNotificationService {

    private final FailedNotificationRepository repository;


    @Transactional
    public void save(String topic, String payload, String eventType,
                     String error) {
        FailedNotification notification = FailedNotification.builder()
                .topic(topic)
                .payload(payload)
                .eventType(eventType)
                .lastError(error)
                .build();
        repository.save(notification);
    }
}
