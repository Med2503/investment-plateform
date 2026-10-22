
package org.bank.fraudservice.config;

import org.apache.kafka.common.TopicPartition;
import org.bank.sharedevents.kafka.kafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<Object, Object> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, exception) -> new TopicPartition(
                                kafkaTopics.TRADE_EXECUTED_DLT,
                                -1
                        )
                );

        // 2 retries after the initial attempt = 3 attempts total.
        FixedBackOff backOff = new FixedBackOff(1_000L, 2L);

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
