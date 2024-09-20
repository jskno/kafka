package com.jskno.consumer;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.CommonClientConfigs.GROUP_INSTANCE_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class D_ConsumerDemoCooperative {

    private static final Logger log = LoggerFactory.getLogger(
        D_ConsumerDemoCooperative.class.getSimpleName());

    public static void main(String[] args) {

        Properties properties = new Properties();

        properties.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        properties.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(GROUP_ID_CONFIG, "my-java-application");
        properties.setProperty(AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());

        // Strategy for static assigment of partitions
//        properties.setProperty(GROUP_INSTANCE_ID_CONFIG, "my-java-application-instance-1")

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...");
            consumer.wakeup();

            // Join the main thread to allow the execution of the code in the main thread
            try {
                mainThread.join();
            } catch (InterruptedException ex) {
                throw new RuntimeException("Error while waiting for the main thread to finish", ex);
            }
        }));

        try {

            // Subscribe the consumer to out topic
            consumer.subscribe(List.of("demo-java"));

            // Poll for new data in a infinite loop
            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

                records.forEach(record -> {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
                });

            }

        } catch (WakeupException ex) {
            // We ignore this as this is an expected exception when closing a consumer
            log.info("Wake up exception");
        } catch (Exception ex) {
            log.error("Unexpected exception");
        } finally {
            consumer.close(); // This will also commit the offsets if need be
            log.info("The consumer is now gracefully closed");
        }

    }

}
