package com.jskno.greetings.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jskno.greetings.constants.GreetingsConstants;
import com.jskno.greetings.exceptionhandler.StreamProcessorCustomErrorHandler;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.streams.RecoveringDeserializationExceptionHandler;

@Configuration
@Slf4j
public class GreetingStreamConfig {

    @Bean
    public NewTopic greetingsTopic() {
        return TopicBuilder
            .name(GreetingsConstants.GREETINGS)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic greetingsOutputTopic() {
        return TopicBuilder
            .name(GreetingsConstants.GREETINGS_OUTPUT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic jsonGreetingsTopic() {
        return TopicBuilder
            .name(GreetingsConstants.JSON_GREETINGS)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic jsonGreetingsOutputTopic() {
        return TopicBuilder
            .name(GreetingsConstants.JSON_GREETINGS_OUTPUT)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs(KafkaProperties kafkaProperties) {
        Map<String, Object> streamProps = kafkaProperties.buildStreamsProperties(null);
        streamProps.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
            RecoveringDeserializationExceptionHandler.class);
        streamProps.put(RecoveringDeserializationExceptionHandler.KSTREAM_DESERIALIZATION_RECOVERER, recoverer());
        return new KafkaStreamsConfiguration(streamProps);
    }

    private ConsumerRecordRecoverer recoverer() {
        return (consumerRecord, e) -> {
            log.error("Kafka deserialization Exception is {}, Failed Record: {}",
                e.getMessage(), consumerRecord, e);
            log.error("FailedRecord: key {} value {}", consumerRecord.key(), consumerRecord.value());
        };
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer streamsBuilderFactoryBeanConfigurer() {
        return factoryBeanConfigurer -> {
            factoryBeanConfigurer.setStreamsUncaughtExceptionHandler(
                new StreamProcessorCustomErrorHandler());
        };
    }

}
