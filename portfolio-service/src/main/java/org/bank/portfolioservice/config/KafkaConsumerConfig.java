package org.bank.portfolioservice.config;


import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.core.KafkaTemplate;


import java.util.HashMap;
import java.util.Map;


@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {


    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {


        Map<String, Object> props = new HashMap<>();


        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "localhost:9092"
        );


        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "portfolio-service-group"
        );


        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );


        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );


        props.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "org.bank.sharedevents.*"
        );


        return new DefaultKafkaConsumerFactory<>(
                props
        );

    }


    @Bean
    public DefaultErrorHandler errorHandler() {


        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate
                );


        ExponentialBackOffWithMaxRetries backOff =
                new ExponentialBackOffWithMaxRetries(3);


        backOff.setInitialInterval(2000);

        backOff.setMultiplier(2);


        return new DefaultErrorHandler(
                recoverer,
                backOff
        );

    }

}
