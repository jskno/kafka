package com.jskno.productsapp.config;

import com.jskno.kafka.event.driven.ProductCreatedEvent;
import com.jskno.kafka.event.driven.ProductCreatedEventV2;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

//@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfigV2 {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    ProducerFactory<String, ProductCreatedEventV2> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean("kafkaTemplateV2")
    KafkaTemplate<String, ProductCreatedEventV2> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
