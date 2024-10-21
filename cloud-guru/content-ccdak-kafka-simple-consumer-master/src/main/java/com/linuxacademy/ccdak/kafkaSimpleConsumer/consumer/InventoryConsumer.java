package com.linuxacademy.ccdak.kafkaSimpleConsumer.consumer;

import com.linuxacademy.ccdak.kafkaSimpleConsumer.constants.AppConstants;
import com.linuxacademy.ccdak.kafkaSimpleConsumer.excetion.AppException;
import com.linuxacademy.ccdak.kafkaSimpleConsumer.model.Inventory;
import com.linuxacademy.ccdak.kafkaSimpleConsumer.serializer.JsonDeserializer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
Caused by: java.lang.RuntimeException: com.fasterxml.jackson.databind.exc.InvalidDefinitionException:
	Cannot construct instance of `com.linuxacademy.ccdak.kafkaSimpleConsumer.model.Inventory` (no Creators, like default constructor, exist):
	cannot deserialize from Object value (no delegate- or property-based Creator)

	This error while deserializing is due to missing noArgs constructor in Inventory class.
 */
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);

    public static void main(String[] args) {
        Properties properties = buildConsumerProperties();

        try(KafkaConsumer<String, Inventory> kafkaConsumer =
            new KafkaConsumer<>(properties, new StringDeserializer(), new JsonDeserializer<>(Inventory.class))) {

            kafkaConsumer.subscribe(Collections.singleton(AppConstants.INVENTORY_TOPIC));

            while (true) {
                ConsumerRecords<String, Inventory> records = kafkaConsumer.poll(Duration.ofMillis(100));
                records.forEach(record -> {
                    log.info("Consumed message: key = {}, value = {}", record.key(), record.value());
                    log.info("Partitions: {}, Offset = {}", record.partition(), record.offset());
                    log.info("Product name: " + record.value().getProduct());
                    log.info("Product quantity: " + record.value().getQuantity());
                });

            }

        }
    }

    private static Properties buildConsumerProperties() {
        Properties properties = readConfig(AppConstants.CONFIG_FILE);
//        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "");
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-processor-app-2");

        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        return properties;
    }

    public static Properties readConfig(final String configFile) {
        // reads the client configuration from client.properties
        // and returns it as a Properties object
        if (!Files.exists(Paths.get(configFile))) {
            throw new AppException(configFile + " not found.");
        }

        final Properties config = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            config.load(inputStream);
        } catch (FileNotFoundException e) {
            throw new AppException(configFile + " not found.");
        } catch (IOException e) {
            throw new RuntimeException(configFile + " not found.");
        }

        return config;
    }

}
