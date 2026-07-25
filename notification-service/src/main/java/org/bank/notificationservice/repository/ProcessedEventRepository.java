package org.bank.notificationservice.repository;

import org.bank.notificationservice.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    Optional<ProcessedEvent> findByEventId(String eventId);

    boolean existsByEventId(String eventId);
}
