package org.bank.transferservice.config;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class kafkaTopicConfig {


    @Bean
    public NewTopic transferCompletedTopic() {
        return TopicBuilder
                .name("transfer-completed")
                .partitions(3)
                .replicas(1)
                .build();
    }

}
