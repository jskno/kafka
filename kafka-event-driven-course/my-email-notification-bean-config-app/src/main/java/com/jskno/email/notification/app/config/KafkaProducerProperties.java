package com.jskno.email.notification.app.config;

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
public class KafkaProducerProperties {

    private String bootstrapServers;

}
