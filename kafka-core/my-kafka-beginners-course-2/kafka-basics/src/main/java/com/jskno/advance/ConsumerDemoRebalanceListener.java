package com.jskno.advance;

import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoRebalanceListener {

    public static final Logger log = LoggerFactory.getLogger(ConsumerDemoRebalanceListener.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I am a Kafka Consumer with Rebalance Listener !!");

        Properties properties = new Properties();

        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(GROUP_ID_CONFIG, "my-java-application");
        properties.setProperty(AUTO_OFFSET_RESET_CONFIG, "earliest");

        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        ConsumerRebalanceListenerImpl listener = new ConsumerRebalanceListenerImpl(consumer);

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...");
            consumer.wakeup();

            try {
                mainThread.join();
            } catch (InterruptedException e) {
                log.error("Error while waiting for the main thread to finish", e);
                throw new RuntimeException("Error while waiting for the main thread to finish", e);
            }
        }));

        try {
            consumer.subscribe(List.of("demo-java"), listener);
            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                records.forEach(record -> {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                    log.info("Partition: " + record.partition() + ", Offset: " + record.offset());

                    // we track the offset we have been committed in the listener
                    listener.addOffsetToTrack(record.topic(), record.partition(), record.offset());
                });

                // We commitAsync as we have processed all data and we don't want to block until the next .poll() call
                consumer.commitAsync();
            }
        } catch (WakeupException e) {
            log.info("Received shutdown signal");
            // we ignore this as this is an expected exception when closing a consumer
        } catch (Exception e) {
                log.error("Unexpected exception", e);
        } finally {
            try {
                consumer.commitSync(listener.getCurrentOffsets()); // we must commit the offsets synchronously here
            } finally {
                consumer.close();
                log.info("The consumer is now gracefully closed.");
            }
        }


    }

}
