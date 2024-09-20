package com.jskno.avro.producer;

import org.springframework.boot.SpringApplication;

public class TestMyAvroProducerTwoSchemaVersionsPureKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.from(MyAvroProducerTwoSchemaVersionsPureKafkaApplication::main).with(TestcontainersConfiguration.class)
            .run(args);
    }

}
