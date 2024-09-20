package com.jskno.consumer;

import com.jskno.avro.generated.CoffeeOrder;
import com.jskno.constants.CoffeeOrderConstants;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeOrderConsumer {

    public static final Logger LOGGER = LoggerFactory.getLogger(CoffeeOrderConsumer.class);

    public static void main(String[] args) {
        LOGGER.info("Starting Coffee Order Consumer...");

        Properties properties = buildConsumerProperties();

        KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(List.of(CoffeeOrderConstants.COFFEE_ORDER_TOPIC));

        while (true) {
            ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(record -> {
                CoffeeOrder coffeeOrder = decodeAvroCoffeeOrder(record.value());
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
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "coffee-orders-consumer");
        return properties;
    }

    private static CoffeeOrder decodeAvroCoffeeOrder(byte[] array) {
        try {
            return CoffeeOrder.fromByteBuffer(ByteBuffer.wrap(array));
        } catch (IOException e) {
            LOGGER.error("Exception is: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
