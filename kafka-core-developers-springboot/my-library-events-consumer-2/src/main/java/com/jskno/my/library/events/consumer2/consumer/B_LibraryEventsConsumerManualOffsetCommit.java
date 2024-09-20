package com.jskno.my.library.events.consumer2.consumer;

import com.jskno.my.library.events.consumer2.domain.LibraryEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(name = "library.events.kafka.startup.b")
@Component
@Slf4j
public class B_LibraryEventsConsumerManualOffsetCommit implements AcknowledgingMessageListener<Long, LibraryEventDTO> {

    @KafkaListener(
        id = "libraryEventManualOffsetListener",
        containerFactory = "kafkaListenerContainerFactoryForB",
        topics = {"${library.events.kafka.topic}"},
//        groupId = "${spring.kafka.consumer.group-id}",
        groupId = "library-events-listener-group-manual",
        autoStartup = "${library.events.kafka.startup.b}"
    )
    public void onMessage(ConsumerRecord<Long, LibraryEventDTO> consumerRecord, Acknowledgment acknowledgment) {
        log.info("From B_LibraryEventsConsumerManualOffsetCommit ConsumerRecord: {}", consumerRecord);
        acknowledgment.acknowledge();
    }

}
