package org.bank.riskservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.entity.OutboxEvent;
import org.bank.riskservice.model.OutboxStatus;
import org.bank.riskservice.properties.OutboxProperties;
import org.bank.riskservice.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;
    private final OutboxProperties properties;

    public OutboxEvent save(OutboxEvent event) {
        return repository.save(event);
    }

    @Transactional
    public void markAsPublished(OutboxEvent event) {
        event.setStatus(OutboxStatus.PUBLISHED);
        event.setPublishedAt(Instant.now());
        repository.save(event);
    }

    @Transactional
    public void markAsFailed(OutboxEvent event, Exception ex) {
        event.setRetryCount(event.getRetryCount() + 1);
        event.setLastError(ex.getMessage());
        if (event.getRetryCount() >= properties.getMaxRetries()) {
            event.setStatus(OutboxStatus.FAILED);
        }
        repository.save(event);
    }
}
