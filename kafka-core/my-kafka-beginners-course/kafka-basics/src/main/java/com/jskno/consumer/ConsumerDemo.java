package com.jskno.consumer;

import static com.jskno.constants.ApplicationConstants.LINE_SEPARATOR;

import com.jskno.constants.ApplicationConstants;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemo {

    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class.getSimpleName());

    public static void main(String[] args) {
        log.info("I am a Kafka Consumer");

        String kafkaServer = "127.0.0.1:9092";
        String groupId = "my-second-application";
        String topic = "demo_java";

        // Create Properties
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Create a consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // Subscribe the consumer to out topic
        consumer.subscribe(Collections.singleton(topic));

        // Poll for new data in a infinite loop
        while (true) {
            log.info("Polling");

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record: records) {
                StringBuilder builder = new StringBuilder();
                builder.append("Key: ").append(record.key()).append(", Value: ").append(record.value()).append(LINE_SEPARATOR);
                builder.append("Partition: ").append(record.partition()).append(", Offset: ").append(record.offset()).append(LINE_SEPARATOR);
                log.info(builder.toString());
            }
        }


    }

}
