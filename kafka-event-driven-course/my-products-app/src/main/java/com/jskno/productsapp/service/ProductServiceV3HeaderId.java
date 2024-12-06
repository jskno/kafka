package com.jskno.productsapp.service;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.kafka.event.driven.ProductCreatedEventV2;
import com.jskno.productsapp.domain.CreateProductRestModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ProductServiceV3HeaderId {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final String topicName;

    public ProductServiceV3HeaderId(
            KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate,
            @Value("${product.created.events.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public String createProduct(CreateProductRestModel product) {
        String productId = UUID.randomUUID().toString();
        // TODO Persist product in DDBB
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .id(productId)
                .title(product.getTitle())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();

        ProducerRecord<String, ProductCreatedEvent> producerRecord = new ProducerRecord<>(topicName, productId, event);
        producerRecord.headers().add("messageId", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        //producerRecord.headers().add("messageId", "12345".getBytes(StandardCharsets.UTF_8));
        producerRecord.headers().add("originApp", "my-product-app".getBytes(StandardCharsets.UTF_8));

        CompletableFuture<SendResult<String, ProductCreatedEvent>> sendResult = kafkaTemplate.send(producerRecord);

        sendResult.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Failed to send message: {}", throwable.getMessage(), throwable);
            } else {
                log.info("Message sent successfully. Topic: {}, Partition:{}, Offset: {}",
                        result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });

        // To make the call sync
        //sendResult.join();

        log.info("***** Returning product id *******");
        return productId;
    }
}
