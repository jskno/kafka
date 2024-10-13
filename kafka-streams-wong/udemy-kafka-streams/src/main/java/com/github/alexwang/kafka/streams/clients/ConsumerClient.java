package com.github.alexwang.kafka.streams.clients;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsumerClient {
    private final static Logger LOG = LoggerFactory.getLogger(ConsumerClient.class);
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String TOPIC = "time.append";


    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(ConsumerConfig.GROUP_ID_CONFIG, "event.time.test");
        conf.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        conf.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        conf.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(conf);
        consumer.subscribe(Collections.singleton(TOPIC));
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean flag = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            flag.set(false);
            latch.countDown();
        }));
        while (flag.get()) {
            consumer.poll(Duration.ofMinutes(1)).forEach(record -> {
                LOG.info("******** record timestamp:{}", record.timestamp());
                LOG.info("record key:{} and value:{}", record.key(), record.value());
            });
        }
        latch.await();
        consumer.close();
        LOG.info("The kafka consumer client is closed...");
    }
}
