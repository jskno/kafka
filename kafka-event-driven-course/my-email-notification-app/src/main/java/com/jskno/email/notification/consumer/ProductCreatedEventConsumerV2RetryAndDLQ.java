package com.jskno.email.notification.consumer;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.kafka.event.driven.ProductCreatedEventV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductCreatedEventConsumerV2RetryAndDLQ {

    @RetryableTopic(dltTopicSuffix = "-my-dlt")
    @KafkaListener(
            groupId = "one-topic-dlq",
            topics = {"${product.created.events.topic}"}
    )
    public void onMessage(ProductCreatedEvent event) {
        log.info("Received product created event from DLQ consumer: {}", event);
    }

}
