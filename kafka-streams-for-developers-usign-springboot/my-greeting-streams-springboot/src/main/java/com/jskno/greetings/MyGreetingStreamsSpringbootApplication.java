package com.jskno.greetings;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class MyGreetingStreamsSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyGreetingStreamsSpringbootApplication.class, args);
    }

}
