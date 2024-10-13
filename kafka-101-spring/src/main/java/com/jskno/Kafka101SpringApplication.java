package com.jskno;

import com.jskno.producer.PurchaseProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;

@SpringBootApplication
public class Kafka101SpringApplication {

    private final PurchaseProducer producer;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    Kafka101SpringApplication(PurchaseProducer producer, KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.producer = producer;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Kafka101SpringApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return (args) -> {
            for (String arg : args) {
                switch (arg) {
                    case "--producer":
                        this.producer.sendMessage("awalther", "t-shirts");
                        this.producer.sendMessage("htanaka", "t-shirts");
                        this.producer.sendMessage("htanaka", "batteries");
                        this.producer.sendMessage("eabara", "t-shirts");
                        this.producer.sendMessage("htanaka", "t-shirts");
                        this.producer.sendMessage("jsmith", "book");
                        this.producer.sendMessage("awalther", "t-shirts");
                        this.producer.sendMessage("jsmith", "batteries");
                        this.producer.sendMessage("jsmith", "gift card");
                        this.producer.sendMessage("eabara", "t-shirts");
                        break;
                    case "--consumer":
                        MessageListenerContainer listenerContainer =
                            kafkaListenerEndpointRegistry.getListenerContainer("purchase-consumer");
                        assert listenerContainer != null;
                        listenerContainer.start();
                        break;
                    default:
                        break;
                }
            }
        };
    }

}
