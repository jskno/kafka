package com.jskno.email.notification.consumer;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

//@Component
@KafkaListener(topics = "${product.created.events.topic}")
@RequiredArgsConstructor
@Slf4j
public class ProductCreatedEventConsumerV4HeaderId {

    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY)String receivedKey,
                       @Header(value = "appOrigin", required = false) String origin) {
        log.info("Received new event in ProductCreatedEventConsumerV4HeaderId");
        log.info("Event with title: {}", productCreatedEvent.getTitle());
        log.info("Event with key: {}", messageId);
        log.info("Event with messageId: {}", receivedKey);
        log.info("Event with origin: {}", origin);
    }

}
