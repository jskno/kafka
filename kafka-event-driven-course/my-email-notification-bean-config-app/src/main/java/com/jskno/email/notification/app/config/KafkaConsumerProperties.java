package com.jskno.email.notification.app.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "my.kafka.consumer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KafkaConsumerProperties {

    private String bootstrapServers;
    private String groupId;
    private String trustedPackages;
    private String autoOffsetReset;
}
