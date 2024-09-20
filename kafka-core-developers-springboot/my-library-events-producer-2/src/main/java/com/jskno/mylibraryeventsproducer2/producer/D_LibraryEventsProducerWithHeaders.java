package com.jskno.mylibraryeventsproducer2.producer;

import com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class D_LibraryEventsProducerWithHeaders {

    private final KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate;
    private final String topic;

    public D_LibraryEventsProducerWithHeaders(KafkaTemplate<Long, LibraryEventDTO> kafkaTemplate, @Value("${library.events.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public CompletableFuture<SendResult<Long, LibraryEventDTO>> sendLibraryEvent(LibraryEventDTO libraryEvent) {
        var producerRecord = buildProducerRecord(libraryEvent);
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
        List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes(StandardCharsets.UTF_8)));
        return new ProducerRecord<Long, LibraryEventDTO>(topic, null, libraryEvent.id(), libraryEvent, recordHeaders);
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
