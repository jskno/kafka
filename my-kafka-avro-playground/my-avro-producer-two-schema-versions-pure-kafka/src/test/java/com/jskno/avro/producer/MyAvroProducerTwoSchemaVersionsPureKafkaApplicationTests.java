package com.jskno.avro.producer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MyAvroProducerTwoSchemaVersionsPureKafkaApplicationTests {

    @Test
    void contextLoads() {
    }

}
