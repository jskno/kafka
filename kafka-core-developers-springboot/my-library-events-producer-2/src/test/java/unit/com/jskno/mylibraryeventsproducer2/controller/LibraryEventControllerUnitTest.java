package com.jskno.mylibraryeventsproducer2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO;
import com.jskno.mylibraryeventsproducer2.producer.A_AsyncLibraryEventsProducer;
import com.jskno.mylibraryeventsproducer2.producer.B_SyncLibraryEventsProducer;
import com.jskno.mylibraryeventsproducer2.producer.C_LibraryEventsProducerWithRecord;
import com.jskno.mylibraryeventsproducer2.producer.D_LibraryEventsProducerWithHeaders;
import com.jskno.mylibraryeventsproducer2.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

// Test Slice
@WebMvcTest(LibraryEventController.class)
class LibraryEventControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private A_AsyncLibraryEventsProducer asyncLibraryEventsProducer;

    @MockBean
    B_SyncLibraryEventsProducer syncLibraryEventsProducer;

    @MockBean
    C_LibraryEventsProducerWithRecord libraryEventsProducerWithRecord;

    @MockBean
    D_LibraryEventsProducerWithHeaders libraryEventsProducerWithHeaders;

    @Test
    void createLibraryEvent() throws Exception {

        // Given
        var json = objectMapper.writeValueAsString(TestUtil.buildLibraryEvent());
        Mockito.when(asyncLibraryEventsProducer.sendLibraryEvent(ArgumentMatchers.isA(LibraryEventDTO.class))).thenReturn(null);

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/library-events")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated());

        // Then

    }

    @Test
    void createLibraryEventWhenInvalidValues() throws Exception {

        // Given
        var json = objectMapper.writeValueAsString(TestUtil.buildLibraryEventWithInvalidBook());
        Mockito.when(asyncLibraryEventsProducer.sendLibraryEvent(ArgumentMatchers.isA(LibraryEventDTO.class))).thenReturn(null);
        var expectedErrorMessage = "book.id - must not be null, book.name - must not be blank";

        // When
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/library-events")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.content().string(expectedErrorMessage));

        // Then

    }

}
