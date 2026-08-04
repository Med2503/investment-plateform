package org.bank.notificationservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.notificationservice.entity.ProcessedEvent;
import org.bank.notificationservice.repository.ProcessedEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final ProcessedEventRepository repository;

    public boolean alreadyProcessed(String eventId) {

        return repository.existsByEventId(eventId);

    }

    @Transactional
    public void markAsProcessed(
            String eventId,
            String eventType
    ) {

        if (repository.existsByEventId(eventId)) {
            return;
        }

        repository.save(
                ProcessedEvent.builder()
                        .eventId(eventId)
                        .eventType(eventType)
                        .build()
        );

    }

    // je l'ai utilisé pour RiskDecisionConsumer => verifier l'unicité puis save() dans un seul bloc .
    @Transactional
    public boolean tryProcess(
            String eventId,
            String eventType
    ) {

        try {

            repository.save(

                    ProcessedEvent.builder()

                            .eventId(eventId)

                            .eventType(eventType)

                            .build()

            );
            return true;

        } catch (DataIntegrityViolationException e) {

            return false;

        }

    }

}
