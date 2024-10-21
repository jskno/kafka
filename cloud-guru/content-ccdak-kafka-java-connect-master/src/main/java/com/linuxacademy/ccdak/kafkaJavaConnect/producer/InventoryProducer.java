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
import org.apache.kafka.common.serialization.StringSerializer;

public class InventoryProducer {

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();
        try (KafkaProducer<String, Inventory> kafkaProducer = new KafkaProducer<>(properties)) {

            for (int i = 0; i < 1000; i++) {
                ProducerRecord<String, Inventory> record = buildProducerRecord();
                kafkaProducer.send(record);
            }
            kafkaProducer.flush();
        }
    }

    private static ProducerRecord<String, Inventory> buildProducerRecord() {
        int random = new Random().nextInt(24) + 1;
        String key = "Product" + random;
        int quantity = new Random().nextInt(1000) + 1;
        return new ProducerRecord<>(AppConstants.INVENTORY_TOPIC, key, new Inventory(key, quantity));
    }

    private static Properties buildProducerProperties() {
        Properties properties = readConfig(AppConstants.CONFIG_FILE);
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
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
