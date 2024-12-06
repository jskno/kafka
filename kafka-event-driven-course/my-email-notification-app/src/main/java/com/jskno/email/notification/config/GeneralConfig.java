package com.jskno.email.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GeneralConfig {

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
