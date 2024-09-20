package com.jskno.my.library.events.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.my.library.events.consumer.entity.LibraryEvent;
import com.jskno.my.library.events.consumer.jpa.LibraryEventsRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LibraryEventsService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LibraryEventsRepository libraryEventsRepository;

    public void processLibraryEvent(ConsumerRecord<Integer, String> consumerRecord) throws JsonProcessingException {
        LibraryEvent libraryEvent = objectMapper.readValue(consumerRecord.value(), LibraryEvent.class);
        log.info("LibraryEvent: {}", libraryEvent);

        if (libraryEvent != null && libraryEvent.getLibraryEventId() != null && libraryEvent.getLibraryEventId().equals(999)) {
            throw new RecoverableDataAccessException("Temporary Network Issue");
        }

        switch (libraryEvent.getLibraryEventType()) {
            case NEW:
                save(libraryEvent);
                break;
            case UPDATE:
                validate(libraryEvent);
                save(libraryEvent);
                break;
            default:
                log.info("Invalid Library Event Type");
        }

    }

    private void save(LibraryEvent libraryEvent) {
        libraryEvent.getBook().setLibraryEvent(libraryEvent);
        libraryEventsRepository.save(libraryEvent);
        log.info("Successfully persisted the library event {}", libraryEvent);
    }

    private void validate(LibraryEvent libraryEvent) {
        if (libraryEvent.getLibraryEventId() == null) {
            throw new IllegalArgumentException("LibraryEventId is missing");
        }

        Optional<LibraryEvent> ddbbLibraryEvent = libraryEventsRepository.findById(libraryEvent.getLibraryEventId());
        if (!ddbbLibraryEvent.isPresent())  {
            throw new IllegalArgumentException("Not a valid library event");
        }
        log.info("Validation successful for the Library Event: {}", ddbbLibraryEvent.get());
    }

}
