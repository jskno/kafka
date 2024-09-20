package com.jskno.avro.consumer;

import org.springframework.boot.SpringApplication;

public class TestMyAvroConsumerTwoSchemaVersionsPureKafkaApplication {

    public static void main(String[] args) {
        SpringApplication.from(MyAvroConsumerTwoSchemaVersionsPureKafkaApplication::main).with(TestcontainersConfiguration.class)
            .run(args);
    }

}
