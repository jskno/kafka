package com.jskno.producer;

import com.jskno.avro.generated.CoffeeOrderComplexIdV3;
import com.jskno.avro.generated.OrderId;
import com.jskno.constants.CoffeeOrderConstants;
import com.jskno.utils.CoffeeOrderComplexIdNoneCompatibilityUtil;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeOrderProducerComplexIdNoneCompatibility {

    public static final Logger LOGGER = LoggerFactory.getLogger(CoffeeOrderProducerComplexIdNoneCompatibility.class);

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();

        KafkaProducer<OrderId, CoffeeOrderComplexIdV3> producer = new KafkaProducer<>(properties);
        CoffeeOrderComplexIdV3 coffeeOrder = CoffeeOrderComplexIdNoneCompatibilityUtil.buildNewCoffeeOrder();
        ProducerRecord<OrderId, CoffeeOrderComplexIdV3> record = buildProducerRecord(coffeeOrder);

        try {
            RecordMetadata recordMetadata = producer.send(record).get();
            LOGGER.info("Record sent to topic: {}, partition: {}, offset: {}",
                recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
            LOGGER.info("Published the record {}", record);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    private static Properties buildProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Changes to get id also as AVRO schema
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());

        // CHANGES NEEDED TWO SWITCHED TO SCHEMA REGISTRY
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        // This URL comes from dockerCompose file
        properties.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
        return properties;
    }

    private static ProducerRecord<OrderId, CoffeeOrderComplexIdV3> buildProducerRecord(CoffeeOrderComplexIdV3 coffeeOrder) {
        return new ProducerRecord<>(
            CoffeeOrderConstants.COFFEE_ORDER_COMPLEX_ID_NONE_COMPATIBILITY_NEW_TOPIC,
            coffeeOrder.getId(),
            coffeeOrder);
    }

}
