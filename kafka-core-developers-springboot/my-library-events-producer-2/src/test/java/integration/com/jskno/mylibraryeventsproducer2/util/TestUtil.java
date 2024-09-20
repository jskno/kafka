package com.jskno.mylibraryeventsproducer2.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.mylibraryeventsproducer2.domain.BookDTO;
import com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO;
import com.jskno.mylibraryeventsproducer2.domain.LibraryEventType;

public class TestUtil {

    public static LibraryEventDTO buildLibraryEvent() {
        return new LibraryEventDTO(null, LibraryEventType.NEW, buildBook());
    }

    public static LibraryEventDTO buildLibraryEventWithId() {
        return new LibraryEventDTO(123L, LibraryEventType.NEW, buildBook());
    }

    public static LibraryEventDTO buildLibraryEventUpdate() {
        return new LibraryEventDTO(123L, LibraryEventType.UPDATE, buildBook());
    }

    public static LibraryEventDTO buildLibraryEventWithInvalidBook() {
        return new LibraryEventDTO(null, LibraryEventType.NEW, buildBookWithInvalidValues());
    }

    private static BookDTO buildBook() {
        return new BookDTO(123L, "Dilip", "Kafka Using Spring Boot");
    }

    private static BookDTO buildBookWithInvalidValues() {
        return new BookDTO(null, "", "Kafka Using Spring Boot");
    }

    public static LibraryEventDTO parseLibraryEvent(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readValue(json, LibraryEventDTO.class);
        } catch (JsonProcessingException ex) {
            throw  new RuntimeException(ex);
        }
    }

}
