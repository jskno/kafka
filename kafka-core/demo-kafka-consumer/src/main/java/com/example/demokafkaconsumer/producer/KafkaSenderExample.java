package com.example.demokafkaconsumer.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
class KafkaSenderExample {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    KafkaSenderExample(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    void sendMessage(String message, String topicName) {
        kafkaTemplate.send(topicName, message);
    }
}
