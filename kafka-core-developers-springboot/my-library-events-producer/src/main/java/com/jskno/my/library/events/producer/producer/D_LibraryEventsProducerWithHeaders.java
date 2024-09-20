package com.jskno.my.library.events.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.my.library.events.producer.domain.LibraryEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class D_LibraryEventsProducerWithHeaders {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;

    public D_LibraryEventsProducerWithHeaders(KafkaTemplate<Integer, String> kafkaTemplate, ObjectMapper objectMapper,
        @Value("${spring.kafka.template.default-topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
    }

    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> record = buildProducerRecord(key, value, topicName);
        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(record);
        listenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topicName) {
        List<Header> headers = List.of(
            new RecordHeader("event-source", "scanner".getBytes()),
            new RecordHeader("whatever", "soWhat".getBytes())
        );
        return new ProducerRecord<>(topicName, null, key, value, headers);
    }

    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error sending the message with key: {} and the exception is {}", key, ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error onFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message sent succesfully for the key: {} and the value: {}, partition: {}",
            key, value, result.getRecordMetadata().partition());
    }

}
