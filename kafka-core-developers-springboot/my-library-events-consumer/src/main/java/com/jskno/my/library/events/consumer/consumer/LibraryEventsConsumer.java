package com.jskno.my.library.events.consumer.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jskno.my.library.events.consumer.service.LibraryEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LibraryEventsConsumer {

    @Autowired
    private LibraryEventsService libraryEventsService;

    @KafkaListener(
        topics = {"${topics.library-events}"},
        groupId = "library-events-listener-group",
        autoStartup = "${topics.library-events.startup:true}")
    public void onMessage(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        log.info("ConsumerRecord: {} ", consumerRecord);
        libraryEventsService.processLibraryEvent(consumerRecord);
    }

}
