package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.dto.request.NotificationRequest;
import org.bank.notificationservice.dto.response.NotificationResponse;
import org.bank.notificationservice.entity.Notification;
import org.bank.notificationservice.factory.NotificationFactory;
import org.bank.notificationservice.mapper.NotificationMapper;
import org.bank.notificationservice.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationMapper mapper;
    private final NotificationFactory factory;

    public NotificationResponse save(NotificationRequest request) {
        Notification notification = factory.create(request);
        Notification saved = repository.save(notification);
        return mapper.toResponse(saved);
    }


    public List<NotificationResponse> getUserNotification(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

}
