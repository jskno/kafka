package com.jskno.email.notification.consumer;

import com.jskno.email.notification.entity.ProcessEventEntity;
import com.jskno.email.notification.exception.NotRetryableException;
import com.jskno.email.notification.exception.RetryableException;
import com.jskno.email.notification.repository.ProcessEventRepository;
import com.jskno.kafka.event.driven.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCreatedEventConsumerV6Database {

    private final RestTemplate restTemplate;
    private final ProcessEventRepository processEventRepository;


    @RetryableTopic(
            dltTopicSuffix = "-my-dlt",
            attempts = "2",
            //kafkaTemplate = "retryableTopicKafkaTemplate",
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR
    )
    @KafkaListener(
            groupId = "one-topic-dlq",
            topics = {"${product.created.events.topic}"}
    )
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String receivedKey,
                       @Header(value = "appOrigin", required = false) String origin) {

        log.info("Received new event in ProductCreatedEventConsumerV4HeaderId");
        log.info("Event with title: {}", productCreatedEvent.getTitle());
        log.info("Event with key: {}", messageId);
        log.info("Event with messageId: {}", receivedKey);
        log.info("Event with origin: {}", origin);

        log.info("Event with title: {}", productCreatedEvent.getTitle());

        // Check if this message was already processed before
        Optional<ProcessEventEntity> existingRecord = processEventRepository.findByMessageId(messageId);
        if (existingRecord.isPresent()) {
            log.info("Found a duplicate message id: {}", messageId);
            return;
        }

        if (productCreatedEvent.getTitle().equals("Errors")) {
            throw new NotRetryableException("An error took place. No need to consume this message again.");
        }
        //if(productCreatedEvent.getTitle().equals("NoneError")) {
        //    throw new RetryableException("");
        //}

        String requestUrl = "http://localhost:8088/response/200";
        try {
            ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Received response from a remote service: {}", response.getBody());
            }
        } catch (ResourceAccessException ex) {
            log.error(ex.getMessage());
            throw new RetryableException(ex);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new NotRetryableException(ex);
        }

        try {
            processEventRepository.save(ProcessEventEntity.builder()
                .messageId(messageId)
                .productId(productCreatedEvent.getId())
                .build());
        } catch (DataIntegrityViolationException ex) {
            throw new NotRetryableException(ex);
        }
    }

    @DltHandler
    public void handleDltPayment(
            ProductCreatedEvent productCreatedEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        log.info("Event on dlt topic={}, payload={}", topic, productCreatedEvent);
    }


}
