package com.linuxacademy.ccdak.kafkaJavaConnect.producer;

import com.linuxacademy.ccdak.kafkaJavaConnect.constants.AppConstants;
import com.linuxacademy.ccdak.kafkaJavaConnect.excetion.AppException;
import com.linuxacademy.ccdak.kafkaJavaConnect.model.Inventory;
import com.linuxacademy.ccdak.kafkaJavaConnect.serializer.JsonSerializer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Random;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryProducer {

    private static final Logger log = LoggerFactory.getLogger(InventoryProducer.class);

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();
        try (KafkaProducer<Long, Inventory> kafkaProducer = new KafkaProducer<>(properties)) {

            for (int i = 0; i < 100; i++) {
                ProducerRecord<Long, Inventory> record = buildProducerRecord();
                log.info("Key: " + record.key() + " Value: " + record.value());
                kafkaProducer.send(record);
            }
            kafkaProducer.flush();
            kafkaProducer.close();
        }
    }

    private static ProducerRecord<Long, Inventory> buildProducerRecord() {
        long key = new Random().nextInt(99) + 1;
        String product = "Product" + key;
        int quantity = new Random().nextInt(1000) + 1;
        return new ProducerRecord<>(AppConstants.INVENTORY_TOPIC, key, new Inventory(key, product, quantity));
    }

    private static Properties buildProducerProperties() {
        Properties properties = readConfig(AppConstants.CONFIG_FILE);
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
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
