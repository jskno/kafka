package com.jskno.opensearch;

import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerUtils {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerUtils.class.getSimpleName());

    public static KafkaConsumer<String, String> createKafkaConsumer() {
        return createKafkaConsumer("true");
    }

    public static KafkaConsumer<String, String> createKafkaConsumer(String autoCommit) {
        String groupId = "consumer-opensearch-demo";

        Properties properties = new Properties();

        // Create consumer configs
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);

        return new KafkaConsumer<>(properties);
    }

    public static void addShutdownHook(KafkaConsumer<String, String> kafkaConsumer) {
        // Get a reference to the current thread
        final Thread mainThread = Thread.currentThread();

        // Adding the shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Detected a shutdown, let's exit by calling consumer.wakeup()...");
                kafkaConsumer.wakeup();

                // Join the main thread to allow the execution of the code in the main thread
                try {
                    mainThread.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

}
