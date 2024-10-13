package com.jskno.producer;

import static com.jskno.constants.Kafka101Constants.PURCHASES_TOPIC;

import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class PurchaseProducer {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public PurchaseProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String key, String value) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(PURCHASES_TOPIC, key, value);

        future.whenComplete((sendResult, ex) -> {
            if (ex == null) {
                logger.info(String.format("Produced event to topic %s: key = %-10s value = %s", sendResult.getRecordMetadata().topic(), key, value));
            } else {
                ex.printStackTrace(System.out);
            }
        });
    }

}
