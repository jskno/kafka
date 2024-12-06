package com.jskno.productsapp.service;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;

@SpringBootTest
public class IdempotentProducerIntegrationTest {

    @Autowired
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    // The test class is nearly complete. One final change I’ll make to it is to mock the KafkaAdmin bean.
    // Please add the following two lines above the test method name:
    // The KafkaAdmin class in Spring Kafka simplifies administrative tasks related to Kafka topics, including creating, deleting,
    // and inspecting topics within a Kafka cluster.
    // When the Products Microservice Spring Boot application starts, it creates a new topic.
    // Since this particular test method does not work with topics, I can mock the KafkaAdmin bean so that it does not communicate
    // with the Kafka cluster. This approach will make my test method run faster.
    @MockitoBean
    KafkaAdmin kafkaAdmin;

    @Test
    void testProducerConfig_whenIdempotenceEnabled_assertsIdempotentProperties() {

        // Arrange
        ProducerFactory<String, ProductCreatedEvent> producerFactory = kafkaTemplate.getProducerFactory();

        // Act
        Map<String, Object> config = producerFactory.getConfigurationProperties();

        // Assert
        Assertions.assertTrue((Boolean.parseBoolean(config.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG).toString())));
        Assertions.assertTrue("all".equalsIgnoreCase((String) config.get(ProducerConfig.ACKS_CONFIG)));

        if (config.containsKey(ProducerConfig.RETRIES_CONFIG)) {
            Assertions.assertTrue(
                    Integer.parseInt(config.get(ProducerConfig.RETRIES_CONFIG).toString()) > 0
            );
        }

    }

}
