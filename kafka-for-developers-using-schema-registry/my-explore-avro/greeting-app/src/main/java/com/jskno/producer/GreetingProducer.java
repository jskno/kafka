package com.jskno.producer;

import com.jskno.avro.Greeting;
import com.jskno.constants.GreetingConstants;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreetingProducer {

    public static final Logger log = LoggerFactory.getLogger(GreetingProducer.class);

    public static void main(String[] args) {
        Properties properties = buildProducerPropertioes();

        KafkaProducer<String, byte[]> producer = new KafkaProducer<>(properties);
        Greeting greeting = buildGreeting("Hello, Schema Registry!!");
        ProducerRecord<String, byte[]> producerRecord = buildProducerRecord(greeting);

        try {
            var metadata = producer.send(producerRecord).get();
            log.info("RecordMetadata: {}", metadata);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    private static Properties buildProducerPropertioes() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        return properties;
    }

    private static Greeting buildGreeting(String message) {
        return Greeting.newBuilder().setGreeting(message).build();
    }

    private static ProducerRecord<String, byte[]> buildProducerRecord(Greeting greeting) {
        try {
            return new ProducerRecord<>(GreetingConstants.GREETING_TOPIC, greeting.toByteBuffer().array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
