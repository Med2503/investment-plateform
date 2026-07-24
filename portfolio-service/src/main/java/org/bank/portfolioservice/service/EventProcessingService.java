package org.bank.portfolioservice.service;


import lombok.RequiredArgsConstructor;
import org.bank.portfolioservice.entity.ProcessedEvent;
import org.bank.portfolioservice.repository.ProcessedEventRepository;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class EventProcessingService {


    private final ProcessedEventRepository repository;


    public boolean alreadyProcessed(
            UUID eventId
    ) {

        return repository.existsByEventId(
                eventId
        );

    }


    public void markProcessed(
            UUID eventId,
            String eventType
    ) {


        repository.save(
                ProcessedEvent.builder()

                        .eventId(eventId)

                        .eventType(eventType)

                        .processedAt(
                                Instant.now()
                        )

                        .build()
        );

    }

}