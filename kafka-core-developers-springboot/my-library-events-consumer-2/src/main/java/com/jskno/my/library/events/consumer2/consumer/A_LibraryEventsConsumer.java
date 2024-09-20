package com.jskno.my.library.events.consumer2.consumer;

import com.jskno.my.library.events.consumer2.domain.LibraryEventDTO;
import com.jskno.my.library.events.consumer2.service.LibraryEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class A_LibraryEventsConsumer {

    private final LibraryEventService libraryEventService;

    @KafkaListener(
        id = "libraryEventListener",
//        containerFactory = "kafkaListenerContainerFactoryForA",
        topics = {"${library.events.kafka.topic}"},
//        groupId = "${spring.kafka.consumer.group-id}",
        groupId = "library-events-listener-group",
//        errorHandler = "errorHandler",
        autoStartup = "${library.events.kafka.startup.a}"
    )
    public void onMessage(ConsumerRecord<Long, LibraryEventDTO> consumerRecord) {
        log.info("From A_LibraryEventsConsumer ConsumerRecord: {}", consumerRecord);
        libraryEventService.processLibraryEvent(consumerRecord.value());
    }

}
