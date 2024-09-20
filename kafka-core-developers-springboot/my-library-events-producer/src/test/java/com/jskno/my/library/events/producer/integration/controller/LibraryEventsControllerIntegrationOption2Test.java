package com.jskno.my.library.events.producer.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jskno.my.library.events.producer.domain.Book;
import com.jskno.my.library.events.producer.domain.LibraryEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// Option 2 only the simple annotations plus active profile test is enough
// if all properties needed are set in the properties file
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@ActiveProfiles("test")
public class LibraryEventsControllerIntegrationOption2Test {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${spring.kafka.template.default-topic}")
    String topic;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> config = new HashMap<>(KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker));
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        consumer = new DefaultKafkaConsumerFactory<>(config, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    @Timeout(5)
    void postLibraryEvent() {

        // Given
        Book book = Book.builder()
            .bookId(456)
            .author("Dilip")
            .title("Kafka Using Spring Boot").build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(null)
            .book(book).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent);

        // When
        ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange(
            "/v1/library-events",
            HttpMethod.POST,
            request,
            LibraryEvent.class);

        // Then
        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

        ConsumerRecords<Integer, String> records = KafkaTestUtils.getRecords(consumer);
        assert records.count() == 1;
        records.forEach(record-> {
            Assertions.assertEquals(
                "{\"libraryEventId\":null,\"libraryEventType\":null,\"book\":{\"bookId\":456,\"title\":\"Kafka Using Spring Boot\",\"author\":\"Dilip\"}}",
                record.value());
        });
    }

    @Test
    @Timeout(5)
    void putLibraryEvent() throws InterruptedException {
        //given
        Book book = Book.builder()
            .bookId(456)
            .author("Dilip")
            .title("Kafka using Spring Boot")
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(123)
            .book(book)
            .build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON.toString());
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        //when
        ResponseEntity<LibraryEvent> responseEntity = restTemplate.exchange(
            "/v1/library-events", HttpMethod.PUT, request, LibraryEvent.class);

        //then
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);
        //Thread.sleep(3000);
        assert consumerRecords.count() >= 1;
        consumerRecords.forEach(record-> {
            if(record.key()!=null){
                String expectedRecord = "{\"libraryEventId\":123,\"libraryEventType\":\"UPDATE\",\"book\":{\"bookId\":456,\"title\":\"Kafka using Spring Boot\",\"author\":\"Dilip\"}}";
                String value = record.value();
                assertEquals(expectedRecord, value);
            }
        });

    }

}
