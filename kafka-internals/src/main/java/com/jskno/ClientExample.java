package com.jskno;

import com.jskno.utils.IOUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientExample {

    private static final Logger log = LoggerFactory.getLogger(ClientExample.class.getSimpleName());

    public static void main(String[] args) {
        try {
            String topic = "perf-test-topic";
            final Properties config = readConfig("client.properties");

            produce(topic, config);
            consume(topic, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Properties readConfig(final String configFile) throws IOException {
        // reads the client configuration from client.properties
        // and returns it as a Properties object
//        if (!Files.exists(Paths.get(configFile))) {
//            throw new IOException(configFile + " not found.");
//        }

        final Properties config = new Properties();
        try (InputStream inputStream = IOUtils.getFileFromResourceAsStream(configFile)) {
            config.load(inputStream);
        }

        return config;
    }

    public static void produce(String topic, Properties config) {
        // sets the message serializers
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // creates a new producer instance and sends a sample message to the topic
        String key = "key";
        String value = "value";
        Producer<String, String> producer = new KafkaProducer<>(config);
        producer.send(new ProducerRecord<>(topic, key, value));
        System.out.printf(
            "Produced message to topic %s: key = %s value = %s%n", topic, key, value
        );

        // closes the producer connection
        producer.close();
    }

    public static void consume(String topic, Properties config) {
        // sets the group ID, offset and message deserializers
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "java-group-1");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // creates a new consumer instance and subscribes to messages from the topic
        try (final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config)) {
            addShutdownHook(consumer);
            consumer.subscribe(Collections.singletonList(topic));

            while (true) {
                // polls the consumer for new messages and prints them
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf(
                        "Consumed message from topic %s: key = %s value = %s%n", topic, record.key(), record.value()
                    );
                }
            }
        } catch (WakeupException ex) {
            // We ignore this as this is an expected exception when closing a consumer
            log.info("Received shutdown signal!");
            log.info("Consumer is starting to shut down...");
        } catch (Exception ex) {
            log.error("Unexpected exception", ex);
        }
    }

    private static void addShutdownHook(Consumer<String, String> consumer) {
        // Get a reference to the current thread
        final Thread mainThread = Thread.currentThread();


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
    }
}
