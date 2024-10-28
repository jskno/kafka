package com.jskno.schemaregistry;

import com.jskno.avro.model.Person;
import com.jskno.constants.AppConstants;
import com.jskno.exceptions.AppException;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
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
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class SchemaRegistryConsumerMain {

    public static void main(String[] args) {

        Properties properties = buildConsumerProperties();
        try (KafkaConsumer<String, Person> kafkaConsumer = new KafkaConsumer<>(properties)) {

            kafkaConsumer.subscribe(Collections.singleton("employees"));

            while (true) {
                ConsumerRecords<String, Person> records = kafkaConsumer.poll(Duration.ofMillis(100));
                for (final ConsumerRecord<String, Person> record : records) {
                    final String key = record.key();
                    final Person value = record.value();
                    System.out.println("key=" + key + ", value=" + value);
                }
            }
        }
    }

    private static Properties buildConsumerProperties() {
        Properties properties = readConfig();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        properties.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
//        properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "");
//        properties.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, "");

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "employees-avro-consumer");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        return properties;
    }

    private static Properties readConfig() {
        if(!Files.exists(Paths.get(AppConstants.CONFIG_FILE))) {
            throw new AppException("File not found");
        }

        Properties properties = new Properties();
        try(InputStream is = new FileInputStream(AppConstants.CONFIG_FILE)) {
            properties.load(is);
        } catch (FileNotFoundException e) {
            throw new AppException("File missing", e);
        } catch (IOException e) {
            throw new AppException("File not ready", e);
        }

        return properties;
    }

}
