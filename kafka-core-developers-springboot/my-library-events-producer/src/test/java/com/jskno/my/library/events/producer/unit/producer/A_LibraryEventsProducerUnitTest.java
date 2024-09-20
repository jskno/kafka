package com.jskno.my.library.events.producer.unit.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.my.library.events.producer.domain.Book;
import com.jskno.my.library.events.producer.domain.LibraryEvent;
import com.jskno.my.library.events.producer.producer.A_LibraryEventsProducer;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

@ExtendWith(MockitoExtension.class)
public class A_LibraryEventsProducerUnitTest {

    @InjectMocks
    A_LibraryEventsProducer libraryEventsProducer;

    @Mock
    KafkaTemplate<Integer, String> kafkaTemplate;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void sendLibraryEvent_WhenFailure() {

        // Given
        Book book = Book.builder()
            .bookId(456)
            .author("Dilip")
            .title("Kafka Using Spring Boot").build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(null)
            .book(book).build();

        SettableListenableFuture future = new SettableListenableFuture();
        future.setException(new RuntimeException("Exception calling Kafka"));
        when(kafkaTemplate.sendDefault(any(), isA(String.class))).thenReturn(future);

        // When
        var exception = Assertions.assertThrows(Exception.class, () ->
            libraryEventsProducer.sendLibraryEvent(libraryEvent).get()
        );

        // Then
        assertTrue(exception.getMessage().contains("Exception calling Kafka"));
    }

    @Test
    public void sendLibraryEvent_WhenSuccess() throws JsonProcessingException, ExecutionException, InterruptedException {

        // Given
        Book book = Book.builder()
            .bookId(456)
            .author("Dilip")
            .title("Kafka Using Spring Boot").build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(null)
            .book(book).build();

        SettableListenableFuture future = new SettableListenableFuture();
        String json = objectMapper.writeValueAsString(libraryEvent);
        ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(
            "library-events", libraryEvent.getLibraryEventId(), json);

        RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("library-events", 1),
            1,1,System.currentTimeMillis(), 1, 2);
        SendResult<Integer, String> sendResult = new SendResult<>(producerRecord, recordMetadata);
        future.set(sendResult);
        when(kafkaTemplate.sendDefault(any(), isA(String.class))).thenReturn(future);

        // When
        ListenableFuture<SendResult<Integer, String>> listenableFuture = libraryEventsProducer.sendLibraryEvent(libraryEvent);

        // Then
        SendResult<Integer, String> result = listenableFuture.get();
        assertEquals(1, result.getRecordMetadata().partition());
    }

}
