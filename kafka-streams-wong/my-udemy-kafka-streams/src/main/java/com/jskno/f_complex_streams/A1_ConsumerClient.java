package com.jskno.f_complex_streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class A1_ConsumerClient {

    public static void main(String[] args) {

        Properties props = buildProperties();
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of("time-default", "time-append"));

        final AtomicBoolean flag = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            flag.set(false);
        }));
        while (flag.get()) {
            consumer.poll(Duration.ofMinutes(1)).forEach(record -> {
                log.info("Topic: {}", record.topic());
                log.info("Record timestamp: {}", record.timestamp());
                log.info("Record key: {}, record value: {}", record.key(), record.value());
            });
        }

        consumer.close();
        log.info("Consumer closed");


    }

    private static Properties buildProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "event-time-consumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return props;
    }
}
