package com.jskno.payments.service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${payments.events.topic.name}")
    private String paymentsEventsTopic;

    @Bean
    NewTopic paymentsEventsTopic() {
        return TopicBuilder.name(paymentsEventsTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }
}
