package com.jskno.producer;

import com.jskno.avro.generated.CoffeeOrder;
import com.jskno.constants.CoffeeOrderConstants;
import com.jskno.utils.CoffeeOrderUtil;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeOrderProducerSchemaRegistry {

    public static final Logger LOGGER = LoggerFactory.getLogger(CoffeeOrderProducerSchemaRegistry.class);

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();

        KafkaProducer<String, CoffeeOrder> producer = new KafkaProducer<>(properties);
        CoffeeOrder coffeeOrder = CoffeeOrderUtil.buildNewCoffeeOrder();
        ProducerRecord<String, CoffeeOrder> record = buildProducerRecord(coffeeOrder);

        try {
            RecordMetadata recordMetadata = producer.send(record).get();
            LOGGER.info("Record sent to topic: {}, partition: {}, offset: {}",
                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    private static Properties buildProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // CHANGES NEEDED TWO SWITCHED TO SCHEMA REGISTRY
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        // This URL comes from dockerCompose file
        properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        return properties;
    }

    private static ProducerRecord<String, CoffeeOrder> buildProducerRecord(CoffeeOrder coffeeOrder) {
        return new ProducerRecord<>(
            CoffeeOrderConstants.COFFEE_ORDER_SCHEMA_REGISTRY_TOPIC,
            String.valueOf(coffeeOrder.getId()),
            coffeeOrder);
    }

}
