package com.jskno.avro.producer.producer;

import com.jskno.confluent.cloud.model.customer.Customer;
import com.jskno.confluent.cloud.model.customer.CustomerV2;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.stream.LongStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

@Slf4j
public class KafkaCustomerProducerV2 {

    private static final String CUSTOMER_TOPIC = "topic-customer";

    private static KafkaProducer<Long, CustomerV2> kafkaProducer;

    public static void main(String[] args) {

        Properties properties = buildProperties();
        kafkaProducer = new KafkaProducer<>(properties);

        List<ProducerRecord<Long, CustomerV2>> producerRecords = buildRecords();

        List<Future<RecordMetadata>> futures = producerRecords.stream()
            .map(KafkaCustomerProducerV2::sendData)
            .toList();

        kafkaProducer.flush();
        kafkaProducer.close();
    }

    private static Properties buildProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        properties.put("schema.registry.url", "http://localhost:8081");
        return properties;
    }

    private static List<ProducerRecord<Long, CustomerV2>> buildRecords() {
        List<ProducerRecord<Long, CustomerV2>> customers = new ArrayList<>();
        LongStream.range(0, 5).forEach(index -> {
                var producerRecord = new ProducerRecord<>(
                    CUSTOMER_TOPIC,
                    index,
                    new CustomerV2(index, "name" + index, "email1235" + index));
                producerRecord.headers().add("CONSUMER_TYPE", "CUSTOMER_V2".getBytes(StandardCharsets.UTF_8));
                customers.add(producerRecord);
            }
        );
        return customers;
    }

    private static Future<RecordMetadata> sendData(ProducerRecord<Long, CustomerV2> producerRecord) {
        return kafkaProducer.send(producerRecord, new CustomerProducerCallback());
    }

    private static class CustomerProducerCallback implements Callback {

        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {
                log.error("Exception while sending data, topic: {}, exception: {}", recordMetadata.topic(), e.getMessage(), e);
            } else {
                log.info("Send message successfully, offset {}", recordMetadata.offset());

            }
        }
    }

}
