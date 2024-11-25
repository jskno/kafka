package com.jskno.email.notification.app.consumer;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
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
}
