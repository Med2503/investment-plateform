package org.bank.riskservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.riskservice.entity.OutboxEvent;
import org.bank.riskservice.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;

    public OutboxEvent save(OutboxEvent event) {
        return repository.save(event);
    }
}
