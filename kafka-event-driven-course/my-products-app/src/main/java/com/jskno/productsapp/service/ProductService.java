package com.jskno.productsapp.service;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.productsapp.domain.CreateProductRestModel;
import com.jskno.productsapp.domain.ErrorCode;
import com.jskno.productsapp.exception.ProductException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        // To make the call sync
        //sendResult.join();

        log.info("***** Returning product id *******");
        return productId;
    }

    public String createProductSync(CreateProductRestModel product) {
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
                log.info("Message sent successfully: {}", result.getRecordMetadata());
            }
        });

        // To make the call sync
        sendResult.join();

        log.info("***** Returning product id *******");
        return productId;
    }

    public String createProductSync2(CreateProductRestModel product) {
        String productId = UUID.randomUUID().toString();
        // TODO Persist product in DDBB
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .id(productId)
                .title(product.getTitle())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();

        log.info("***** Before sending  product event to Kafka *******");

        try {
            SendResult<String, ProductCreatedEvent> sendResult = kafkaTemplate
                    .send(topicName, productId, event).get();
            log.info("TopicName: {}, Partition: {}, Offset: {}", sendResult.getRecordMetadata().topic(),
                    sendResult.getRecordMetadata().partition(), sendResult.getRecordMetadata().offset());
        } catch (InterruptedException e) {
            throw new ProductException(ErrorCode.KAFKA_BROKER_ERROR, e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        log.info("***** Returning product id *******");
        return productId;
    }
}
