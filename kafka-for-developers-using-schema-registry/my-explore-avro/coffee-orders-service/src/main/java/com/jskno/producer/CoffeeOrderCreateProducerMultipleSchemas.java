package com.jskno.producer;

import com.jskno.avro.generated.CoffeeOrderCreate;
import com.jskno.avro.generated.OrderId;
import com.jskno.constants.CoffeeOrderConstants;
import com.jskno.utils.CoffeeOrderMultyplesSchemasUtil;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.subject.RecordNameStrategy;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoffeeOrderCreateProducerMultipleSchemas {

    public static final Logger LOGGER = LoggerFactory.getLogger(CoffeeOrderCreateProducerMultipleSchemas.class);

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();

        KafkaProducer<OrderId, CoffeeOrderCreate> producer = new KafkaProducer<>(properties);
        CoffeeOrderCreate coffeeOrder = CoffeeOrderMultyplesSchemasUtil.buildNewCoffeeOrder();
        ProducerRecord<OrderId, CoffeeOrderCreate> record = buildProducerRecord(coffeeOrder);

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
        properties.put(KafkaAvroDeserializerConfig.VALUE_SUBJECT_NAME_STRATEGY, RecordNameStrategy.class.getName());
        return properties;
    }

    private static ProducerRecord<OrderId, CoffeeOrderCreate> buildProducerRecord(CoffeeOrderCreate coffeeOrder) {
        return new ProducerRecord<>(
            CoffeeOrderConstants.COFFEE_ORDER_COMPLEX_MULTIPLE_SCHEMAS_TOPIC,
            coffeeOrder.getId(),
            coffeeOrder);
    }

}
