package com.jskno.management.orders.streams.config;

import com.jskno.management.orders.streams.constants.OrdersConstants;
import com.jskno.management.orders.streams.exceptionhandler.StreamsProcessorCustomerErrorHandler;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.streams.RecoveringDeserializationExceptionHandler;

@Configuration
@Slf4j
public class OrdersStreamConfiguration {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ServerProperties serverProperties;
    private final String applicationName;

    public OrdersStreamConfiguration(KafkaProperties kafkaProperties, KafkaTemplate<String, String> kafkaTemplate,
        ServerProperties serverProperties, @Value("${spring.application.name}") String applicationName) {
        this.kafkaProperties = kafkaProperties;
        this.kafkaTemplate = kafkaTemplate;
        this.serverProperties = serverProperties;
        this.applicationName = applicationName;
    }

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfiguration() throws UnknownHostException {
        var streamProperties = kafkaProperties.buildStreamsProperties(null);

        streamProperties.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
            RecoveringDeserializationExceptionHandler.class);
        streamProperties.put(RecoveringDeserializationExceptionHandler.KSTREAM_DESERIALIZATION_RECOVERER, consumerRecordRecoverer);

        streamProperties.put(StreamsConfig.APPLICATION_SERVER_CONFIG,
            InetAddress.getLocalHost().getHostAddress() + ":" + serverProperties.getPort());
        streamProperties.put(StreamsConfig.STATE_DIR_CONFIG, String.format("%s%s", applicationName, serverProperties.getPort()));


        return new KafkaStreamsConfiguration(streamProperties);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer streamsBuilderFactoryBeanConfigurer() {
        log.info("Inside streamsBuilderFactoryBeanConfigurer");
        return factoryBean ->
            factoryBean.setStreamsUncaughtExceptionHandler(new StreamsProcessorCustomerErrorHandler());
    }

    public DeadLetterPublishingRecoverer recoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate,
            (consumerRecord, ex) -> {
                log.error("Exception in Deserializing teh message: {} and the record is {}", ex.getMessage(), consumerRecord, ex);
                return new TopicPartition("recovererDLQ", consumerRecord.partition());
            });
    }

    private final ConsumerRecordRecoverer consumerRecordRecoverer = (consumerRecord, e) -> {
        log.error("Exception is {}. Failed record: {}", e, consumerRecord);
    };

    @Bean
    public NewTopic ordersTopicBuilder() {
        return TopicBuilder
            .name(OrdersConstants.ORDERS)
            .partitions(2)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic storesTopicBuilder() {
        return TopicBuilder
            .name(OrdersConstants.STORES)
            .partitions(2)
            .replicas(1)
            .build();
    }

}
