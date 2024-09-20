package com.jskno.consumer;

import static com.jskno.constants.ApplicationConstants.LINE_SEPARATOR;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemoWithShutdownInsideConsumerGroupCooperative {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDemoWithShutdownInsideConsumerGroupCooperative.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I am a Kafka Consumer");

        String kafkaServer = "127.0.0.1:9092";
        String groupId = "my-third-application";
        String topic = "demo_java";

        // Create Properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, CooperativeStickyAssignor.class.getName());
        // To use static assignment of consumers to partitions
//        properties.setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "my-group-always-same-partition");

        // Create a consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // Get a reference to the current thread
        final Thread mainThread = Thread.currentThread();

        // Adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...");
                consumer.wakeup();

                // Join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });

        try {
            // Subscribe the consumer to out topic
            consumer.subscribe(Collections.singleton(topic));

            // Poll for new data in a infinite loop
            while (true) {

                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record: records) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("Key: ").append(record.key()).append(", Value: ").append(record.value()).append(LINE_SEPARATOR);
                    builder.append("Partition: ").append(record.partition()).append(", Offset: ").append(record.offset()).append(LINE_SEPARATOR);
                    log.info(builder.toString());
                }
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
