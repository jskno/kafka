package com.jskno.schemaregistry;

import com.jskno.avro.model.Person;
import com.jskno.constants.AppConstants;
import com.jskno.exceptions.AppException;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaRegistryProducerV3BackwardCompatibeMain {

    private static final Logger log = LoggerFactory.getLogger(SchemaRegistryProducerV3BackwardCompatibeMain.class);

    public static void main(String[] args) {

        Properties properties = buildProducerProperties();

        try(KafkaProducer<String, Person> kafkaProducer = new KafkaProducer<>(properties)) {

            Person kenny = new Person(125745, "Kenny3", "Armstrong", "kenny@linuxacademy.com", "@kenny");
            kafkaProducer.send(new ProducerRecord<>("employees", String.valueOf(kenny.getId()), kenny));

            Person terry = new Person(943256, "Terry3", "Cox", "terry@linuxacademy.com", "@terry");
            kafkaProducer.send(new ProducerRecord<>("employees", String.valueOf(terry.getId()), terry));
        } catch (SerializationException ex) {
            log.error("Serialization error: {}", ex.getMessage(), ex);
            throw ex;
        }

    }

    private static Properties buildProducerProperties() {
        Properties properties = readConfig();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        properties.put(AbstractKafkaSchemaSerDeConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
//        properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "");
//        properties.put(AbstractKafkaSchemaSerDeConfig.USER_INFO_CONFIG, "");

        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
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
