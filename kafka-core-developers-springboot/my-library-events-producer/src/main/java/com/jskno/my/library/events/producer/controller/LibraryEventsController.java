package com.jskno.my.library.events.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jskno.my.library.events.producer.domain.LibraryEvent;
import com.jskno.my.library.events.producer.domain.LibraryEventType;
import com.jskno.my.library.events.producer.exceptions.LibraryIdNotFoundException;
import com.jskno.my.library.events.producer.producer.A_LibraryEventsProducer;
import com.jskno.my.library.events.producer.producer.C_LibraryEventsProducerWithProducerRecord;
import com.jskno.my.library.events.producer.producer.B_SyncLibraryEventsProducer;
import com.jskno.my.library.events.producer.producer.D_LibraryEventsProducerWithHeaders;
import java.util.concurrent.ExecutionException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LibraryEventsController {

    private final A_LibraryEventsProducer libraryEventsProducer;
    private final B_SyncLibraryEventsProducer syncLibraryEventsProducer;
    private final C_LibraryEventsProducerWithProducerRecord libraryEventsProducerWithProducerRecord;
    private final D_LibraryEventsProducerWithHeaders libraryEventsProducerWithHeaders;

    @PostMapping("v1/library-events")
    public ResponseEntity<LibraryEvent> createLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent)
        throws JsonProcessingException {

        log.info("Before sendLibraryEvent");
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        // invoke kafka producer
        libraryEventsProducer.sendLibraryEvent(libraryEvent);
        log.info("After sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("v1/sync-library-events")
    public ResponseEntity<LibraryEvent> createSyncLibraryEvent(@RequestBody LibraryEvent libraryEvent)
        throws JsonProcessingException, ExecutionException, InterruptedException {

        log.info("Before createSyncLibraryEvent");
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        // invoke kafka producer
        SendResult<Integer, String> sendResult = syncLibraryEventsProducer.sendLibraryEvent(libraryEvent);
        log.info("SendResult is {}", sendResult);
        log.info("After createSyncLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("v1/record-library-events")
    public ResponseEntity<LibraryEvent> createLibraryEventWithRecord(@RequestBody LibraryEvent libraryEvent)
        throws JsonProcessingException {

        log.info("Before createLibraryEventWithRecord");
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        // invoke kafka producer
        libraryEventsProducerWithProducerRecord.sendLibraryEvent(libraryEvent);
        log.info("After createLibraryEventWithRecord");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("v1/headers-library-events")
    public ResponseEntity<LibraryEvent> createLibraryEventWithHeader(@RequestBody LibraryEvent libraryEvent)
        throws JsonProcessingException {

        log.info("Before createLibraryEventWithHeader");
        // invoke kafka producer
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventsProducerWithHeaders.sendLibraryEvent(libraryEvent);
        log.info("After createLibraryEventWithHeader");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping("v1/library-events")
    public ResponseEntity<LibraryEvent> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent)
        throws JsonProcessingException {

        if (libraryEvent.getLibraryEventId() == null) {
            throw new LibraryIdNotFoundException();
        }
        // invoke Kafka Producer
        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventsProducer.sendLibraryEvent(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

}
