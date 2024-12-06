package com.jskno.email.notification.consumer;

import com.jskno.email.notification.exception.NotRetryableException;
import com.jskno.email.notification.exception.RetryableException;
import com.jskno.kafka.event.driven.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

//@Component
@KafkaListener(topics = "${product.created.events.topic}")
@RequiredArgsConstructor
@Slf4j
public class ProductCreatedEventConsumerV3HandleError {

    private final RestTemplate restTemplate;

    @KafkaHandler
    public void handle(ProductCreatedEvent productCreatedEvent) {
        log.info("Received new event in ProductCreatedEventConsumerV3HandleError");
        log.info("Event with title: {}", productCreatedEvent.getTitle());
        if (productCreatedEvent.getTitle().equals("Errors")) {
            throw new NotRetryableException("An error took place. No need to consume this message again.");
        }
        //if(productCreatedEvent.getTitle().equals("NoneError")) {
        //    throw new RetryableException("");
        //}

        String requestUrl = "http://localhost:8088/response/500";
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
    }
}
