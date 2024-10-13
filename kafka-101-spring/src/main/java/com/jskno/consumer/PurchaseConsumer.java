package com.jskno.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class PurchaseConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseConsumer.class);

    @KafkaListener(
        id = "purchase-consumer",
        topics = "${kafka.purchases.topic}",
        groupId = "${spring.kafka.consumer.group-id}",
        autoStartup = "false"
    )
    public void onMessage(
        String value,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_KEY) String key) {

        LOGGER.info(String.format("Consumed event from topic %s: key = %-10s value = %s", topic, key, value));

    }

}
