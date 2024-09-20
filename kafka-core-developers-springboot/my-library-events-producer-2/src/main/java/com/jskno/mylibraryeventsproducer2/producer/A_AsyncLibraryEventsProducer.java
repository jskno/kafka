package com.jskno.mylibraryeventsproducer2.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class A_AsyncLibraryEventsProducer {

    private final KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate;
    private final String topic;

    public A_AsyncLibraryEventsProducer(
        KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate, @Value("${library.events.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<SendResult<Long, LibraryEventDTO>> sendLibraryEvent(LibraryEventDTO libraryEvent) throws JsonProcessingException {

        // 1. For the very first time --> Blocking call - Get metadata about the Kafka cluster
        //  (after the first time the first step is skipped: metadata refresh happens in a dedicated interval)
        // 2. Send message happens --> Returns a CompletableFuture
        CompletableFuture<SendResult<Long, LibraryEventDTO>> completableFuture = kafkaTemplate
            .send(topic, libraryEvent.id(), libraryEvent);

        return completableFuture.whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(libraryEvent, throwable);
            } else {
                handleSuccess(libraryEvent, sendResult);
            }

        });
    }

    private void handleFailure(LibraryEventDTO libraryEventDTO, Throwable ex) {
        log.error("Error sending the message with key: {} and the exception is {}", libraryEventDTO.id(), ex.getMessage(), ex);
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error onFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(LibraryEventDTO libraryEventDTO, SendResult<Long, LibraryEventDTO> sendResult) {
        log.info("Message sent succesfully for the key: {} and the value: {}, partition: {}, offset {}",
            libraryEventDTO.id(), libraryEventDTO, sendResult.getRecordMetadata().partition(), sendResult.getRecordMetadata().offset());
    }

}
