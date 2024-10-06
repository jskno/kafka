package com.jskno.consumer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class A_ConsumerDemo {

    private static final Logger log = LoggerFactory.getLogger(A_ConsumerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I am a Kafka Consumer");

        Properties properties = new Properties();

        // Connect to localhost
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Create consumer configs
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "my-java-application");

        // none: if we don't have any existing consumer group then we fail. We must set the consumer group before starting the application.
        // earliest: read from the beginning of the topic (--from-beginning)
        // latest: read only new messages onwards
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // none/earliest/latest

        // Create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // Subscribe consumer to our topic(s)
        consumer.subscribe(List.of("demo-java"));
        consumer.subscribe(Pattern.compile("topic/.+"));
//        consumer.subscribe(Pattern.compile("topic\..+"));

        // Poll for new data in a infinite loop
        while (true) {

            log.info("Polling");
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

            records.forEach(record -> {
                log.info("Key: " + record.key() + ", Value: " + record.value());
                log.info("Partition: " + record.partition() + ", Offset: " + record.offset());
            });

        }
    }

}
