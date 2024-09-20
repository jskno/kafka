package com.jskno.avro.consumer.consumer;

import com.jskno.avro.consumer.service.CustomerV1Service;
import com.jskno.avro.consumer.service.CustomerV2Service;
import com.jskno.confluent.cloud.model.customer.CustomerV2;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.header.Header;
import org.springframework.util.Assert;

@Slf4j
public class CustomerConsumerV1AndV2 {

    private static final String CUSTOMER_TOPIC = "topic-customer";
    private static final CustomerV1Service customerV1Service = new CustomerV1Service();
    private static final CustomerV2Service customerV2Service = new CustomerV2Service();

    public static void main(String[] args) {

        Properties properties = buildConsumerProperties();
        KafkaConsumer<Long, GenericRecord> customerConsumer = new KafkaConsumer<>(properties);
        addShutdownHook(customerConsumer);

        try {

            customerConsumer.subscribe(List.of(CUSTOMER_TOPIC));

            while (true) {
                ConsumerRecords<Long, GenericRecord> consumerRecords = customerConsumer.poll(Duration.ofMillis(1000));
                log.info("Received {} record(s)", consumerRecords.count());

                consumerRecords.forEach(record -> {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                    Iterable<Header> consumerType = record.headers().headers("CONSUMER_TYPE");
                    List<String> customerTypes = StreamSupport.stream(consumerType.spliterator(), false)
                        .map(header -> new String(header.value()))
                        .toList();
                    if (customerTypes.contains("CUSTOMER_V1")) {
                        customerV1Service.processCustomer(record.value());
                    } else if (customerTypes.contains("CUSTOMER_V2")) {
                        customerV2Service.processCustomer(record.value());
                    }
                });
            }

        } catch (WakeupException ex) {
            // We ignore this as this is an expected exception when closing a consumer
            log.info("Received shutdown signal!");
            log.info("Consumer is starting to shut down...");
        } catch (Exception ex) {
            log.error("Unexpected exception", ex);
        } finally {
            // This will also commit the offsets if need be
            customerConsumer.close();
            log.info("The consumer is now gracefully closed");
        }
    }

    private static Properties buildConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "customer-consumer");
        properties.put("schema.registry.url", "http://localhost:8081");
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, false);

        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return properties;
    }


    private static void addShutdownHook(KafkaConsumer<Long, GenericRecord> customerConsumer) {
        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
                public void run() {
                    log.info("Detected a shoutdown, let´s exist by calling consumer.wakeup()...");
                    customerConsumer.wakeup();

                    try {
                        mainThread.join();
                    } catch (InterruptedException ex) {
                        log.error("Error in shutting down hook");
                    }
            }

        });
    }

}
