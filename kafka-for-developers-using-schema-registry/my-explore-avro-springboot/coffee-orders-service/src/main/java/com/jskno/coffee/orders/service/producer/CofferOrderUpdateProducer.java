package com.jskno.coffee.orders.service.producer;

import com.jskno.avro.generated.CoffeeOrderUpdate;
import com.jskno.avro.generated.OrderId;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CofferOrderUpdateProducer {

    private final KafkaTemplate<OrderId, CoffeeOrderUpdate> kafkaTemplate;
    private final String coffeeOrderTopic;

    public CofferOrderUpdateProducer(KafkaTemplate<OrderId, CoffeeOrderUpdate> kafkaTemplate,
        @Value("${kafka.coffee.orders.topic.name}") String coffeeOrderTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.coffeeOrderTopic = coffeeOrderTopic;
    }

    public void sendMessage(CoffeeOrderUpdate coffeeOrderUpdateAvro) {
        ProducerRecord<OrderId, CoffeeOrderUpdate> coffeeRecord = buildProducerRecord(coffeeOrderUpdateAvro);

        CompletableFuture<SendResult<OrderId, CoffeeOrderUpdate>> completableFuture = kafkaTemplate.send(coffeeRecord);
        completableFuture.whenComplete((sendResult, throwable) -> {
            if (throwable == null) {
                handleSuccess(sendResult);
            } else {
                handleFailure(sendResult, throwable);
            }
        });
    }

    private ProducerRecord<OrderId, CoffeeOrderUpdate> buildProducerRecord(CoffeeOrderUpdate coffeeOrderUpdate) {
        return new ProducerRecord<>(coffeeOrderTopic, coffeeOrderUpdate.getId(), coffeeOrderUpdate);
    }

    private void handleSuccess(SendResult<OrderId, CoffeeOrderUpdate> sendResult) {
        OrderId key = sendResult.getProducerRecord().key();
        CoffeeOrderUpdate value = sendResult.getProducerRecord().value();
        log.info("Message sent successfully for the key: {}, value: {}, and partition {}",
            key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(SendResult<OrderId, CoffeeOrderUpdate> sendResult, Throwable throwable) {
        OrderId key = sendResult.getProducerRecord().key();
        CoffeeOrderUpdate value = sendResult.getProducerRecord().value();
        log.error("Error sending the message for key {}, value {}, and the exception is {}",
            key, value, throwable.getMessage(), throwable);
    }


}
