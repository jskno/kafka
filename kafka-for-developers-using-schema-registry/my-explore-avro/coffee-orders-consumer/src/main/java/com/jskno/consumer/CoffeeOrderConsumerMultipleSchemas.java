package com.jskno.consumer;

import com.jskno.avro.generated.CoffeeOrderComplexId;
import com.jskno.avro.generated.CoffeeOrderCreate;
import com.jskno.avro.generated.CoffeeOrderUpdate;
import com.jskno.avro.generated.OrderId;
import com.jskno.constants.CoffeeOrderConstants;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeOrderConsumerMultipleSchemas {

    public static final Logger LOGGER = LoggerFactory.getLogger(CoffeeOrderConsumerMultipleSchemas.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Coffee Order Consumer...");

        Properties properties = buildConsumerProperties();

        KafkaConsumer<OrderId, GenericRecord> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(List.of(CoffeeOrderConstants.COFFEE_ORDER_COMPLEX_MULTIPLE_SCHEMAS_TOPIC));

        while (true) {
            ConsumerRecords<OrderId, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
            for(ConsumerRecord<OrderId, GenericRecord> record: records) {
                LOGGER.info("Consumed Message, key: {}, value: {}", record.key(), record.value().toString());
                var genericRecord = record.value();
                if (genericRecord instanceof CoffeeOrderCreate) {
                    var coffeeOrder = (CoffeeOrderCreate) genericRecord;
                    LOGGER.info("CoffeeOrder name: {}, initial status: {}", coffeeOrder.getName(), coffeeOrder.getStatus());
                } else {
                    var coffeeOrderUpdate = (CoffeeOrderUpdate) genericRecord;
                    LOGGER.info("New status is: {}", coffeeOrderUpdate.getStatus());
                }
            }
        }

    }

    private static Properties buildConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Changes to get id also as AVRO schema
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "coffee-orders-consumer-cid-multiples");

        // CHANGES NEEDED TWO SWITCHED TO SCHEMA REGISTRY
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        // This is going to make sure when the consumer reads the byte[] record is parsed to CoffeeOrder object.
        // If we don't provide this flag to true the consumer will fail to process the record because
        // it will try to parse to a org.apache.avro.generic.GenericData$Record
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        return properties;
    }

}
