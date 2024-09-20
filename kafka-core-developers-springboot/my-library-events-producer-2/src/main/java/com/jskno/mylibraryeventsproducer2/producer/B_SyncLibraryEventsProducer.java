package com.jskno.mylibraryeventsproducer2.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO;
import com.jskno.mylibraryeventsproducer2.exception.LibraryEventException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class B_SyncLibraryEventsProducer {

    private final KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate;
    private final String topic;

    public B_SyncLibraryEventsProducer(KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate, @Value("${library.events.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public SendResult<Long, LibraryEventDTO> sendLibraryEvent(LibraryEventDTO libraryEvent) throws JsonProcessingException {

        try {
            // 1. For the very first time --> Blocking call - Get metadata about the Kafka cluster
            //  (after the first time the first step is skipped: metadata refresh happens in a dedicated interval)
            // 2. Blocks and waits until the message is sent to the Kafka
            var sendResult = kafkaTemplate.send(topic, libraryEvent.id(), libraryEvent).get(3, TimeUnit.SECONDS);
            handleSuccess(libraryEvent, sendResult);
            return sendResult;
        } catch (InterruptedException | ExecutionException | TimeoutException ex) {
            log.error("InterruptedException/ExecutionException/TimeoutException sending the message with key: {} and the exception is {}",
                libraryEvent.id(), ex.getMessage());
            throw new LibraryEventException(
                String.format("InterruptedException/ExecutionException/TimeoutException sending the message with key: %s and the exception is %s",
                libraryEvent.id(), ex.getMessage()), ex);
        }
    }

    private void handleSuccess(LibraryEventDTO libraryEventDTO, SendResult<Long, LibraryEventDTO> sendResult) {
        log.info("Message sent succesfully for the key: {} and the value: {}, partition: {}, offset: {}",
            libraryEventDTO.id(), libraryEventDTO, sendResult.getRecordMetadata().partition(), sendResult.getRecordMetadata().offset());
    }

}
