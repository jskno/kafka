package com.jskno.my.library.events.consumer2.consumer;

import com.jskno.my.library.events.consumer2.domain.LibraryEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "library.events.kafka.startup.c")
@Component
@Slf4j
public class C_LibraryEventsConcurrentConsumer {

    @KafkaListener(
        id = "libraryEventConcurrentListener",
        containerFactory = "kafkaListenerContainerFactoryForC",
        topics = "${library.events.kafka.topic}",
//        groupId = "${spring.kafka.consumer.group-id}",
        groupId = "library-events-listener-group-concurrent",
//        concurrency = "10",
        autoStartup = "${library.events.kafka.startup.c}"
    )
    public void onMessage(ConsumerRecord<Long, LibraryEventDTO> consumerRecord) {
        log.info("From C_LibraryEventsConcurrentConsumer ConsumerRecord: {}", consumerRecord);
    }
}
