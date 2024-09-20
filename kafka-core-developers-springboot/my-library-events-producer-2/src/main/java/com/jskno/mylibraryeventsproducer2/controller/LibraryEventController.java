package com.jskno.mylibraryeventsproducer2.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jskno.mylibraryeventsproducer2.domain.LibraryEventDTO;
import com.jskno.mylibraryeventsproducer2.domain.LibraryEventType;
import com.jskno.mylibraryeventsproducer2.domain.UpdateOperation;
import com.jskno.mylibraryeventsproducer2.exception.LibraryEventException;
import com.jskno.mylibraryeventsproducer2.producer.A_AsyncLibraryEventsProducer;
import com.jskno.mylibraryeventsproducer2.producer.B_SyncLibraryEventsProducer;
import com.jskno.mylibraryeventsproducer2.producer.C_LibraryEventsProducerWithRecord;
import com.jskno.mylibraryeventsproducer2.producer.D_LibraryEventsProducerWithHeaders;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/library-events")
@Slf4j
@RequiredArgsConstructor
public class LibraryEventController {

    private final A_AsyncLibraryEventsProducer libraryEventsProducer;
    private final B_SyncLibraryEventsProducer syncLibraryEventsProducer;
    private final C_LibraryEventsProducerWithRecord libraryEventsProducerWithRecord;
    private final D_LibraryEventsProducerWithHeaders libraryEventsProducerWithHeaders;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LibraryEventDTO> createLibraryEvent(
        @RequestBody @Valid LibraryEventDTO libraryEvent) throws JsonProcessingException {

        log.info("LibraryEvent to be created: {}", libraryEvent);
        log.info("Before sendLibraryEvent");
        // invoke kafka producer
        libraryEventsProducer.sendLibraryEvent(libraryEvent);
        log.info("After sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping(value = "/sync", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LibraryEventDTO> createSyncLibraryEvent(@RequestBody @Valid LibraryEventDTO libraryEvent)
        throws JsonProcessingException {

        log.info("LibraryEvent to be created: {}", libraryEvent);
        log.info("Before sendLibraryEvent");
        // invoke kafka producer
        SendResult<Long, LibraryEventDTO> sendResult = syncLibraryEventsProducer.sendLibraryEvent(libraryEvent);
        log.info("SendResult is {}", sendResult);
        log.info("After sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("/record")
    public ResponseEntity<LibraryEventDTO> createLibraryEventWithRecord(@RequestBody @Valid LibraryEventDTO libraryEvent) {

        log.info("LibraryEvent to be created: {}", libraryEvent);
        log.info("Before sendLibraryEvent");
        // invoke kafka producer
        libraryEventsProducerWithRecord.sendLibraryEvent(libraryEvent);
        log.info("After sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PostMapping("/headers")
    public ResponseEntity<LibraryEventDTO> createLibraryEventWithHeaders(@RequestBody @Valid LibraryEventDTO libraryEvent) {

        log.info("LibraryEvent to be created: {}", libraryEvent);
        log.info("Before sendLibraryEvent");
        // invoke kafka producer
        libraryEventsProducerWithHeaders.sendLibraryEvent(libraryEvent);
        log.info("After sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LibraryEventDTO> updateLibraryEvent(@RequestBody @Validated(UpdateOperation.class) LibraryEventDTO libraryEvent)  {

        log.info("LibraryEvent to be created: {}", libraryEvent);

        if (!LibraryEventType.UPDATE.equals(libraryEvent.type())) {
            throw new LibraryEventException("Only UPDATE event type is supported");
        }
        log.info("Before sendLibraryEvent");
        // invoke kafka producer
        libraryEventsProducerWithHeaders.sendLibraryEvent(libraryEvent);
        log.info("After sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

}
