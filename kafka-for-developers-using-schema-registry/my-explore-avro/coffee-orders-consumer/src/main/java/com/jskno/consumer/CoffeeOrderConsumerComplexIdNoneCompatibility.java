package com.jskno.consumer;

import com.jskno.avro.generated.CoffeeOrderComplexIdV3;
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
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeOrderConsumerComplexIdNoneCompatibility {

    public static final Logger LOGGER = LoggerFactory.getLogger(CoffeeOrderConsumerComplexIdNoneCompatibility.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Coffee Order Consumer...");

        Properties properties = buildConsumerProperties();

        KafkaConsumer<OrderId, CoffeeOrderComplexIdV3> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(List.of(CoffeeOrderConstants.COFFEE_ORDER_COMPLEX_ID_NONE_COMPATIBILITY_NEW_TOPIC));

        while (true) {
            ConsumerRecords<OrderId, CoffeeOrderComplexIdV3> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                CoffeeOrderComplexIdV3 coffeeOrder = record.value();
                LOGGER.info("Consumed Message, key: {}, value: {}", record.key(), coffeeOrder.toString());

                // Bear in mind that the kafka events hold the time that was created as
                // Instant.now() by the CofferOrderUtil, that represents UCT time: 2024-09-14T12:52:35.923214700Z
                // Now we retrieved that UTC time and convert it to the zone we want: 2024-09-14T14:52:35.923
                LocalDateTime utcDateTime = LocalDateTime.ofInstant(coffeeOrder.getOrderTime(), ZoneOffset.UTC);
                LOGGER.info("OrderTime in UTC time: {}", utcDateTime);
                ZoneId zoneId = ZoneId.of(ZoneId.SHORT_IDS.get("ECT"));
                LocalDateTime spainDateTime = LocalDateTime.ofInstant(coffeeOrder.getOrderTime(), zoneId);
                LOGGER.info("OrderTime in Spain local time: {}", spainDateTime);
                // consumer.CoffeeOrderConsumer: OrderTime in UTC time: 2024-09-14T12:57:33.350
                // consumer.CoffeeOrderConsumer: OrderTime in Spain local time: 2024-09-14T14:57:33.350
            });
        }

    }

    private static Properties buildConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Changes to get id also as AVRO schema
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "coffee-orders-consumer-cid-none");

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
