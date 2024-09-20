package com.jskno.my.library.events.consumer2.config;

import com.jskno.my.library.events.consumer2.exceptions.LibraryEventConsumerException;
import com.jskno.my.library.events.consumer2.exceptions.LibraryEventRecoverableException;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.BackOff;

@Configuration
@Slf4j
public class LibraryEventThirdRetryConsumerConfig {

    @Value("${library.events.kafka.dlt.topic}")
    private String deadLetterTopic;

    @Autowired
    KafkaTemplate<Object, Object> kafkaTemplate;

    @Bean("kafkaListenerContainerFactoryForThirdRetry")
    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
        ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
        ConsumerFactory<Object, Object> kafkaConsumerFactory) {

        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        configurer.configure(factory, kafkaConsumerFactory);
        Objects.requireNonNull(factory);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    public CommonErrorHandler errorHandler() {

        var exceptionsToBeIgnored = List.of(
            LibraryEventConsumerException.class
        );

        var exceptionsToRetry = List.of(
            LibraryEventRecoverableException.class
        );

        var errorHandler = new DefaultErrorHandler(publishingRecoverer(), exponentialBackOff());

        exceptionsToBeIgnored.forEach(errorHandler::addNotRetryableExceptions);
        exceptionsToRetry.forEach(errorHandler::addRetryableExceptions);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("Failed Record in Retry Listener, Exception: {}, deliveryAttempt: {}",
                ex.getMessage(), deliveryAttempt);
        });
        return errorHandler;
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
                // Not Recovery Logic
                return new TopicPartition(deadLetterTopic, r.partition());
            });
    }

}
