package com.jskno.my.library.events.producer.unit.controller;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.my.library.events.producer.controller.LibraryEventsController;
import com.jskno.my.library.events.producer.domain.Book;
import com.jskno.my.library.events.producer.domain.LibraryEvent;
import com.jskno.my.library.events.producer.producer.A_LibraryEventsProducer;
import com.jskno.my.library.events.producer.producer.B_SyncLibraryEventsProducer;
import com.jskno.my.library.events.producer.producer.C_LibraryEventsProducerWithProducerRecord;
import com.jskno.my.library.events.producer.producer.D_LibraryEventsProducerWithHeaders;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class LibraryEventsControllerUnitTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    @MockBean
    A_LibraryEventsProducer libraryEventsProducer;

    @MockBean
    B_SyncLibraryEventsProducer syncLibraryEventsProducer;

    @MockBean
    C_LibraryEventsProducerWithProducerRecord libraryEventsProducerWithProducerRecord;

    @MockBean
    D_LibraryEventsProducerWithHeaders libraryEventsProducerWithHeaders;

    @Test
    void postLibraryEvent_WhenBookIsNull() throws Exception {
        // Given
        Book book = Book.builder()
            .bookId(456)
            .author("Dilip")
            .title("Kafka Using Spring Boot").build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(null)
            .book(null).build();

        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);

        // When
        mockMvc.perform(post("/v1/library-events")
            .content(json)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("book - must not be null"));
        // Then
    }

    @Test
    void postLibraryEvent_WhenAuthorIsBlank() throws Exception {
        // Given
        Book book = Book.builder()
            .bookId(null)
            .author("")
            .title("Kafka Using Spring Boot").build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(null)
            .book(book).build();

        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);

        // When
        mockMvc.perform(post("/v1/library-events")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(
                "book.author - must not be blank, book.bookId - must not be null"));
        // Then
    }

    @Test
    void postLibraryEvent_WhenCompleteBook() throws Exception {
        // Given
        Book book = Book.builder()
            .bookId(456)
            .author("Dilip")
            .title("Kafka Using Spring Boot").build();
        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(null)
            .book(book).build();

        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);

        // When
        mockMvc.perform(post("/v1/library-events")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
        // Then
    }

    @Test
    void updateLibraryEvent() throws Exception {

        //given
        Book book = new Book().builder()
            .bookId(123)
            .author("Dilip")
            .title("Kafka Using Spring Boot")
            .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
            .libraryEventId(123)
            .book(book)
            .build();
        String json = objectMapper.writeValueAsString(libraryEvent);
        when(libraryEventsProducer.sendLibraryEvent(isA(LibraryEvent.class))).thenReturn(null);

        //expect
        mockMvc.perform(
                put("/v1/library-events")
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    }
}
