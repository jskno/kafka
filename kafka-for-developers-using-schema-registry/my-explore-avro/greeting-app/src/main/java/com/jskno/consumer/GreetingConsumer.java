package com.jskno.consumer;

import com.jskno.avro.Greeting;
import com.jskno.constants.GreetingConstants;
import com.jskno.producer.GreetingProducer;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreetingConsumer {

    public static final Logger log = LoggerFactory.getLogger(GreetingConsumer.class);

    public static void main(String[] args) {
        Properties properties = buildConsumerProperties();

        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(GreetingConstants.GREETING_TOPIC));

        log.info("Consumer Started");
        while(true) {

            ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(500));

            records.forEach(record -> {
                Greeting greeting = decodeAvroGreeting(record.value());
                log.info("Consumed Message, key: {}, value: {}", record.key(), greeting.toString());
            });
        }


    }

    private static Properties buildConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "greeting.consumer");

        return properties;
    }

    private static Greeting decodeAvroGreeting(byte[] array) {
        try {
            return Greeting.fromByteBuffer(ByteBuffer.wrap(array));
        } catch (IOException e) {
            log.error("Exception is: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
