package org.bank.notificationservice.repository;

import org.bank.notificationservice.entity.FailedNotification;
import org.bank.notificationservice.entity.FailedNotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FailedNotificationRepository extends JpaRepository<
        FailedNotification,
        UUID> {
    List<FailedNotification> findByStatus(FailedNotificationStatus status);
}
