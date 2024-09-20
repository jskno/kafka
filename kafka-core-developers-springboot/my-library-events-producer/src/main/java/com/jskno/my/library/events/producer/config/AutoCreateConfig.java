package com.jskno.my.library.events.producer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")
public class AutoCreateConfig {

    @Bean
    NewTopic libraryEvents() {
        return TopicBuilder.name("library-events")
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    NewTopic libraryEventsRetry() {
        return TopicBuilder.name("library-events.RETRY")
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    NewTopic libraryEventsDlt() {
        return TopicBuilder.name("library-events.DLT")
            .partitions(3)
            .replicas(3)
            .build();
    }

}
