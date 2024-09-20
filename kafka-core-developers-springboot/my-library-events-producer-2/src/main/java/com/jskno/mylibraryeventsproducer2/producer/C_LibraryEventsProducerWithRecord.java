package com.jskno.mylibraryeventsproducer2.producer;

import com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class C_LibraryEventsProducerWithRecord {

    private final KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate;
    private final String topic;

    public C_LibraryEventsProducerWithRecord(KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate, @Value("${library.events.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<SendResult<Long, LibraryEventDTO>> sendLibraryEvent(LibraryEventDTO libraryEvent) {
        ProducerRecord<Long, LibraryEventDTO> producerRecord = buildProducerRecord(libraryEvent);

        CompletableFuture<SendResult<Long, LibraryEventDTO>> completableFuture = kafkaTemplate.send(producerRecord);

        return completableFuture.whenComplete((sendResult, throwable) -> {
            if (throwable != null) {
                handleFailure(producerRecord, throwable);
            } else {
                handleSuccess(producerRecord, sendResult);
            }

        });

    }

    private ProducerRecord<Long, LibraryEventDTO> buildProducerRecord(LibraryEventDTO libraryEvent) {
        return new ProducerRecord<>(topic, libraryEvent.id(), libraryEvent);
    }

    private void handleFailure(ProducerRecord<Long, LibraryEventDTO> producerRecord, Throwable ex) {
        log.error("Error sending the message with key: {} and the exception is {}", producerRecord.key(), ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error onFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(ProducerRecord<Long, LibraryEventDTO> producerRecord, SendResult<Long, LibraryEventDTO> result) {
        log.info("Message sent succesfully for the key: {} and the value: {}, partition: {}",
            producerRecord.key(), producerRecord.value(), result.getRecordMetadata().partition());
    }
}
