package com.jskno.productsapp.service;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.productsapp.domain.CreateProductRestModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ProductServiceV4Sync {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final String topicName;

    public ProductServiceV4Sync(
            KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate,
            @Value("${product.created.events.topic}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    public String createProduct(CreateProductRestModel product) {
        String productId = UUID.randomUUID().toString();
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .id(productId)
                .title(product.getTitle())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();

        ProducerRecord<String, ProductCreatedEvent> producerRecord = new ProducerRecord<>(topicName, productId, event);
        producerRecord.headers().add("messageId", UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        producerRecord.headers().add("originApp", "my-product-app".getBytes(StandardCharsets.UTF_8));

        try {
            SendResult<String, ProductCreatedEvent> sendResult = kafkaTemplate.send(producerRecord).get();
            log.info("Message sent successfully. Topic: {}, Partition:{}, Offset: {}",
                    sendResult.getRecordMetadata().topic(),
                    sendResult.getRecordMetadata().partition(),
                    sendResult.getRecordMetadata().offset());
        } catch (ExecutionException e) {
            log.error("Failed to send message. Execution Exception: {}", e.getCause().getMessage(), e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.error("Failed to send message. Interrupted Exception exception: {}", e.getCause().getMessage(), e);
            throw new RuntimeException(e);
        }

        log.info("***** Returning product id *******");
        return productId;
    }
}
