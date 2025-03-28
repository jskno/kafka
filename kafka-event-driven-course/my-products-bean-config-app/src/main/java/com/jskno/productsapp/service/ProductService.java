package com.jskno.productsapp.service;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.productsapp.domain.CreateProductRestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final String topicName;

    public ProductService(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate,
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

        CompletableFuture<SendResult<String, ProductCreatedEvent>> sendResult = kafkaTemplate.send(topicName, productId, event);

        sendResult.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Failed to send message: {}", throwable.getMessage(), throwable);
            } else {
                log.info("Message sent successfully. Topic: {}, Partition:{}, Offset: {}",
                        result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });

        log.info("***** Returning product id *******");
        return productId;
    }

}
