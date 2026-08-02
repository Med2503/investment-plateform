package org.bank.riskservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.entity.OutboxEvent;
import org.bank.riskservice.model.OutboxStatus;
import org.bank.riskservice.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;

    public OutboxEvent save(OutboxEvent event) {
        return repository.save(event);
    }

    @Transactional
    public void markAsPublished(OutboxEvent event) {
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(Instant.now());
        repository.save(event);
    }
}
