package com.jskno.email.notification.consumer;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class ProductCreatedEventConsumerV0 {

    @KafkaListener(
            groupId = "one-topic-one-method",
            topics = {"${product.created.events.topic}"}
    )
    public void onMessage(ProductCreatedEvent event) {
        log.info("Received product created event from one method: {}", event);
    }

}
