package com.jskno.email.notification.consumer;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.kafka.event.driven.ProductCreatedEventV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
@KafkaListener(
        groupId = "one-topic-two-types-two-methods",
        topics = {"${product.created.events.topic}"}
)
@Slf4j
public class ProductCreatedEventConsumerV1 {

    @KafkaHandler
    public void onMessage(ProductCreatedEvent event) {
        log.info("Received product created event from two methods: {}", event);
    }

    @KafkaHandler
    public void onMessageV2(ProductCreatedEventV2 event) {
        log.info("Received product created event: {}", event);
    }
}
