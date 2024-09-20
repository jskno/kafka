package com.jskno.my.library.events.consumer2.config;

import com.jskno.my.library.events.consumer2.domain.LibraryEventDTO;
import com.jskno.my.library.events.consumer2.exceptions.LibraryEventConsumerException;
import com.jskno.my.library.events.consumer2.exceptions.LibraryEventRecoverableException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

@ConditionalOnProperty(name = "library.events.kafka.startup.a")
@Configuration
//@EnableKafka (only for older versions of SpringBoot-Kafka)
@Slf4j
public class A_LibraryEventsConsumerConfig {

    @Value("${library.events.kafka.first.retry.topic}")
    private String retryTopic;

    @Value("${library.events.kafka.dlt.topic}")
    private String deadLetterTopic;

    @Autowired
    KafkaTemplate<Object, Object> kafkaTemplate;

//    public ConsumerFactory<Long, LibraryEventDTO> consumerFactory(KafkaProperties kafkaProperties) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group_id");
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());

//        Map<String, Object> properties = kafkaProperties.buildConsumerProperties(null);
//        JsonDeserializer<LibraryEventDTO> jsonDeserializer = new JsonDeserializer<>(LibraryEventDTO.class, false);
//        jsonDeserializer.addTrustedPackages("*");
//
//        return new DefaultKafkaConsumerFactory<>(properties, new LongDeserializer(), jsonDeserializer);
//    }

//    @Bean("kafkaListenerContainerFactoryForA")
//    public ConcurrentKafkaListenerContainerFactory<Long, LibraryEventDTO> kafkaListenerContainerFactory(KafkaProperties kafkaProperties) {
//        ConcurrentKafkaListenerContainerFactory<Long, LibraryEventDTO> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory(kafkaProperties));
//        factory.setCommonErrorHandler(errorHandler());
//        return factory;
//    }

    @Bean("errorHandler")
    public CommonErrorHandler errorHandler() {

        var exceptionsToBeIgnored = List.of(
            LibraryEventConsumerException.class
        );

        var exceptionsToRetry = List.of(
            LibraryEventRecoverableException.class
        );

//        var errorHandler = new DefaultErrorHandler(fixedBackOff());
//        var errorHandler = new DefaultErrorHandler(exponentialBackOff());
        var errorHandler = new DefaultErrorHandler(publishingRecoverer(), exponentialBackOff());

        exceptionsToBeIgnored.forEach(errorHandler::addNotRetryableExceptions);
        exceptionsToRetry.forEach(errorHandler::addRetryableExceptions);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("Failed Record in Retry Listener, Exception: {}, deliveryAttempt: {}",
                ex.getMessage(), deliveryAttempt);
        });
        return errorHandler;
    }

    private BackOff fixedBackOff() {
        // Until 3 attempts with one second interval between calls
        return new FixedBackOff(1000L, 2);
    }

    private BackOff exponentialBackOff() {
        // Until 3 attempts with one second interval between first error and second one,
        // and 2 seconds between the second error and the third one
        var exponentialBackOff = new ExponentialBackOffWithMaxRetries(2);
        exponentialBackOff.setInitialInterval(1_000L);
        exponentialBackOff.setMultiplier(2.0);

        return exponentialBackOff;
    }

    private DeadLetterPublishingRecoverer publishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
            (r, e) -> {
                log.error("Exception in publishingRecover: {}", e);
                if (e.getCause() instanceof LibraryEventRecoverableException) {
                    // Recovery Logic
                    log.error("Inside RecoverableDataAccessException block");
                    return new TopicPartition(retryTopic, r.partition());
                } else {
                    // Not Recovery Logic
                    return new TopicPartition(deadLetterTopic, r.partition());
                }
            });
    }

}
