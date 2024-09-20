package com.jskno.my.library.events.producer.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.my.library.events.producer.domain.LibraryEvent;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class B_SyncLibraryEventsProducer {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public SendResult<Integer, String> sendLibraryEvent(LibraryEvent libraryEvent)
        throws JsonProcessingException, ExecutionException, InterruptedException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        SendResult<Integer, String> sendResult = null;
        try {
            sendResult = kafkaTemplate.sendDefault(key, value).get();
        } catch (InterruptedException | ExecutionException ex) {
            log.error("InterruptedException/ExecutionException sending the message with key: {} and the exception is {}",
                key, ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Exception sending the message with key: {} and the exception is {}", key, ex.getMessage());
            throw ex;
        }
        return sendResult;
    }

}
