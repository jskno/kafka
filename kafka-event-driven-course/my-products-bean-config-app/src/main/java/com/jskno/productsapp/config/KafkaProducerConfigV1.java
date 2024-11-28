package com.jskno.productsapp.config;

import com.jskno.productsapp.domain.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfigV1 {

    private final MyKafkaProperties myKafkaProperties;

    Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, myKafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, myKafkaProperties.getDeliveryTimeoutMs());
        props.put(ProducerConfig.LINGER_MS_CONFIG, myKafkaProperties.getLingerMs());
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, myKafkaProperties.getRequestTimeoutMs());

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, myKafkaProperties.getEnableIdempotence());
        props.put(ProducerConfig.ACKS_CONFIG, myKafkaProperties.getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, myKafkaProperties.getRetries());
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, myKafkaProperties.getMaxInFlightRequestsPerConnection());

        return props;
    }

    ProducerFactory<String, ProductCreatedEvent> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean("kafkaTemplateV1-EventWithDifferentPackage")
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
