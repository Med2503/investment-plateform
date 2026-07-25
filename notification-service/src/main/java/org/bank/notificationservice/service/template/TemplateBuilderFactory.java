package org.bank.notificationservice.service.template;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class TemplateBuilderFactory {


    private final List<EmailTemplateBuilder> builders;


    public EmailTemplateBuilder getBuilder(
            Class<?> eventType
    ) {


        return builders.stream()

                .filter(builder ->
                        builder.supports()
                                .equals(eventType)
                )

                .findFirst()

                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No template builder found for "
                                        + eventType.getSimpleName()
                        )
                );

    }

}
