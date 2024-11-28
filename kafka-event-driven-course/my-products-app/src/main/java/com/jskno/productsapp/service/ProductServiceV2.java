package com.jskno.productsapp.service;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.kafka.event.driven.ProductCreatedEventV2;
import com.jskno.productsapp.domain.CreateProductRestModel;
import com.jskno.productsapp.domain.ErrorCode;
import com.jskno.productsapp.exception.ProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ProductServiceV2 {

    private final KafkaTemplate<String, ProductCreatedEventV2> kafkaTemplate;
    private final String topicName;

    public ProductServiceV2(
            KafkaTemplate<String, ProductCreatedEventV2> kafkaTemplate,
            @Value("${product.created.events.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public String createProduct(CreateProductRestModel product) {
        String productId = UUID.randomUUID().toString();
        // TODO Persist product in DDBB
        ProductCreatedEventV2 event = ProductCreatedEventV2.builder()
                .id(productId)
                .description(product.getTitle())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();

        CompletableFuture<SendResult<String, ProductCreatedEventV2>> sendResult = kafkaTemplate.send(topicName, productId, event);

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
