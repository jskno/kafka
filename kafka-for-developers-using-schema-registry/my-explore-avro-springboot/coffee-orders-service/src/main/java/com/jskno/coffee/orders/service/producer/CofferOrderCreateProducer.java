package com.jskno.coffee.orders.service.producer;

import com.jskno.avro.generated.CoffeeOrderCreate;
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
public class CofferOrderCreateProducer {

    private final KafkaTemplate<OrderId, CoffeeOrderCreate> kafkaTemplate;
    private final String coffeeOrderTopic;

    public CofferOrderCreateProducer(KafkaTemplate<OrderId, CoffeeOrderCreate> kafkaTemplate,
        @Value("${kafka.coffee.orders.topic.name}") String coffeeOrderTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.coffeeOrderTopic = coffeeOrderTopic;
    }

    public void sendMessage(CoffeeOrderCreate coffeeOrderCreateAvro) {
        ProducerRecord<OrderId, CoffeeOrderCreate> coffeeRecord = buildProducerRecord(coffeeOrderCreateAvro);

        CompletableFuture<SendResult<OrderId, CoffeeOrderCreate>> completableFuture = kafkaTemplate.send(coffeeRecord);
        completableFuture.whenComplete((sendResult, throwable) -> {
            if (throwable == null) {
                handleSuccess(sendResult);
            } else {
                handleFailure(sendResult, throwable);
            }
        });
    }

    private ProducerRecord<OrderId, CoffeeOrderCreate> buildProducerRecord(CoffeeOrderCreate coffeeOrderCreate) {
        return new ProducerRecord<>(coffeeOrderTopic, coffeeOrderCreate.getId(), coffeeOrderCreate);
    }

    private void handleSuccess(SendResult<OrderId, CoffeeOrderCreate> sendResult) {
        OrderId key = sendResult.getProducerRecord().key();
        CoffeeOrderCreate value = sendResult.getProducerRecord().value();
        log.info("Message sent successfully for the key: {}, value: {}, and partition {}",
            key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(SendResult<OrderId, CoffeeOrderCreate> sendResult, Throwable throwable) {
        OrderId key = sendResult.getProducerRecord().key();
        CoffeeOrderCreate value = sendResult.getProducerRecord().value();
        log.error("Error sending the message for key {}, value {}, and the exception is {}",
            key, value, throwable.getMessage(), throwable);

    }


}
