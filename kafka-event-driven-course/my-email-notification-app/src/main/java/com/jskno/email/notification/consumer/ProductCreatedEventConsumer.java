package com.jskno.email.notification.consumer;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.kafka.event.driven.ProductCreatedEventV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(
        topics = {"${product.created.events.topic}"}
)
@Slf4j
public class ProductCreatedEventConsumer {

    @KafkaHandler
    public void onMessage(ProductCreatedEvent event) {
        log.info("Received product created event: {}", event);
    }

    @KafkaHandler
    public void onMessageV3(ProductCreatedEventV3 event) {
        log.info("Received product created event: {}", event);
    }
}
