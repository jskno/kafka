package com.jskno.email.notification.consumer;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCreatedEventConsumerV5ConsumingDLQ {

    private final RestTemplate restTemplate;

    @RetryableTopic(
            attempts = "2",
            //kafkaTemplate = "retryableTopicKafkaTemplate",
            dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR)
    @KafkaListener(
            groupId = "let-consume-dlt",
            topics = "${product.created.events.topic}")
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

    @DltHandler
    public void handleDltPayment(
            ProductCreatedEvent productCreatedEvent, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("Event on dlt topic={}, payload={}", topic, productCreatedEvent);
    }

}
