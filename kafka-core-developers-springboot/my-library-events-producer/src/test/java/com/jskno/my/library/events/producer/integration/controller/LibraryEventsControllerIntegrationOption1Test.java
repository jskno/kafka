package com.jskno.my.library.events.producer.integration.controller;

import com.jskno.my.library.events.producer.domain.Book;
import com.jskno.my.library.events.producer.domain.LibraryEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

// Option 1: specify and/or overwrite properties needed in here, test header
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"}, partitions = 3)
@TestPropertySource(properties = {
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.admin.properties.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.template.default-topic=library-events",
})
@ActiveProfiles("none")
public class LibraryEventsControllerIntegrationOption1Test {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void postLibraryEvent() {
        // Given
        Book book = Book.builder()
            .bookId(123)
            .author("Dilip")
            .title("Kafka using SpringBoot").build();
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
    }

    @Test
    void postLibraryEvent_WhenBlankTitle() {
        // Given
        Book book = Book.builder()
            .bookId(123)
            .author("Dilip")
            .title(null).build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(null)
            .book(book).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent);

        // When
        ResponseEntity<String> responseEntity = restTemplate.exchange(
            "/v1/library-events",
            HttpMethod.POST,
            request,
            String.class);

        // Then
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

}
