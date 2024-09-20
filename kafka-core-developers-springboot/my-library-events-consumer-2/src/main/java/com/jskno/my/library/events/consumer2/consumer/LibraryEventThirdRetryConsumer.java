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
public class LibraryEventThirdRetryConsumer {

    private final LibraryEventService libraryEventService;

    @KafkaListener(
        id = "libaryEventThirdRetryListener",
        containerFactory = "kafkaListenerContainerFactoryForThirdRetry",
        groupId = "third-retry-listener-group",
        topics = "${library.events.kafka.third.retry.topic}",
//        errorHandler = "thirdRetryErrorHandler",
        autoStartup = "${library.events.kafka.startup.third.retry.topic}"
    )
    void onMessage(ConsumerRecord<Long, LibraryEventDTO> consumerRecord) {
        log.info("Consumer Record in Retry Consumer: {}", consumerRecord);
        consumerRecord.headers()
            .forEach(header ->
                log.info("Key: {}, value: {}", header.key(), new String(header.value())));
        libraryEventService.processLibraryEvent(consumerRecord.value());
    }

}
