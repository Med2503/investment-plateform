package org.bank.notificationservice.mapper;


import org.bank.notificationservice.dto.response.NotificationResponse;
import org.bank.notificationservice.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {

        return new NotificationResponse(

                notification.getId(),
                notification.getUserId(),
                notification.getChannel(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getStatus(),
                notification.getCreatedAt(),
                notification.getSentAt()

        );

    }
}
