package com.jskno.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jskno.avro.generated.CoffeeOrder;
import com.jskno.constants.CoffeeOrderConstants;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeOrderConsumerGenericRecord {

    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static final Logger LOGGER = LoggerFactory.getLogger(CoffeeOrderConsumerGenericRecord.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Coffee Order Consumer...");

        Properties properties = buildConsumerProperties();


        KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(List.of(CoffeeOrderConstants.COFFEE_ORDER_SCHEMA_REGISTRY_TOPIC));

        while (true) {
            ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                GenericRecord genericRecord = record.value();
                CoffeeOrder coffeeOrder = decodeAvroCoffeeOrderFromGenericRecord(genericRecord);

                LOGGER.info("Consumed Message, key: {}, value: {}", record.key(), genericRecord.toString());
                LOGGER.info("Consumed Message, key: {}, value: {}", record.key(), coffeeOrder.toString());

            });
        }

    }

    private static Properties buildConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "coffee-orders-consumer-sr");

        // CHANGES NEEDED TWO SWITCHED TO SCHEMA REGISTRY
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // By providing this flag to false (default) the consumer will parse the byte[] from the broker to a GenericRecord
        // There are use cases where you may have to do it. If you have multiple schemas in the same topic, you may want to
        // parse the byte[] to a GenericRecord and then decide which schema to use to parse the GenericRecord to a specific class
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);
        return properties;
    }

    private static CoffeeOrder decodeAvroCoffeeOrderFromGenericRecord(GenericRecord genericRecord) {
        try {
            return objectMapper.readValue(genericRecord.toString(), CoffeeOrder.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
