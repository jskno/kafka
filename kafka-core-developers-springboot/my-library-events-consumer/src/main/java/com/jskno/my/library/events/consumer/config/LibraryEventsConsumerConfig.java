package com.jskno.my.library.events.consumer.config;

import com.jskno.my.library.events.consumer.service.FailureService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
@Slf4j
public class LibraryEventsConsumerConfig {

    public static final String RETRY = "RETRY";
    private static final String DEAD = "DEAD";
    public static final String SUCCESS = "SUCCESS";

    @Autowired
    KafkaTemplate<Object, Object> kafkaTemplate;

    @Value("${topics.retry}")
    private String retryTopic;

    @Value("${topics.dlt}")
    private String deadLetterTopic;

    @Autowired
    private FailureService failureService;

    @Bean
    public DefaultErrorHandler defaultErrorHandler() {

//        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 2);
//        DefaultErrorHandler errorHandler = new DefaultErrorHandler(fixedBackOff);

        ExponentialBackOffWithMaxRetries expBackOff = new ExponentialBackOffWithMaxRetries(2);
        expBackOff.setInitialInterval(1_000L);
        expBackOff.setMultiplier(2.0);
        expBackOff.setMaxInterval(2_000L);
//        DefaultErrorHandler errorHandler = new DefaultErrorHandler(publishingRecoverer(), expBackOff);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(consumerRecordRecoverer, expBackOff);


        List<Class<? extends Exception>> notRetryableExceptions = List.of(IllegalArgumentException.class);
        List<Class<? extends Exception>> retryableExceptions = List.of(RecoverableDataAccessException.class);

        notRetryableExceptions.forEach(errorHandler::addNotRetryableExceptions);
        retryableExceptions.forEach(errorHandler::addRetryableExceptions);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.info("Failed Record in Retry Listener, Exception : {}, delivery attempt: {}",
                ex, deliveryAttempt);
        });

        return errorHandler;
    }

    DeadLetterPublishingRecoverer publishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
            (r, e) -> {
                log.error("Exception in publishingRecover: {}", e);
                if (e.getCause() instanceof RecoverableDataAccessException) {
                    // Recovery Logic
                    log.error("Inside RecoverableDataAccessException block");
                    return new TopicPartition(retryTopic, r.partition());
                } else {
                    // Not Recovery Logic
                    return new TopicPartition(deadLetterTopic, r.partition());
                }
            });
    }

//    @Bean
//    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
//    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
//        ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
//        ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        configurer.configure(factory, kafkaConsumerFactory
//            .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.properties.buildConsumerProperties())));
//        return factory;
//    }

    ConsumerRecordRecoverer consumerRecordRecoverer = (consumerRecord, e) -> {
        log.error("Exception in Custom consumerRecordRecoverer: {}", e);
        var record = (ConsumerRecord<Integer, String>) consumerRecord;
        if (e.getCause() instanceof RecoverableDataAccessException) {
            // Recovery Logic
            log.error("Inside Recovery");
            failureService.saveFailedRecord(record, e, RETRY);
        }
        else {
            // Not Recovery Logic
            log.error("Inside Not Recovery");
            failureService.saveFailedRecord(record, e, DEAD);
        }
    };

}
