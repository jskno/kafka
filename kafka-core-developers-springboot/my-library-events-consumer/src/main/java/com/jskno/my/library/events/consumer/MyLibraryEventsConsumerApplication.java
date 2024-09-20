package com.jskno.my.library.events.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyLibraryEventsConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyLibraryEventsConsumerApplication.class, args);
    }

}
