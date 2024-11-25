package com.jskno.productsapp.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "my.kafka.producer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyKafkaProperties {

    private String bootstrapServers;
    private String acks;
    private String retries;
    private String deliveryTimeoutMs;
    private String lingerMs;
    private String requestTimeoutMs;
    private String maxInFlightRequestsPerConnection;
    private String enableIdempotence;


}
