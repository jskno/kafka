package com.jskno.producer;

import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class A_ProducerDemo {

    private static final Logger log = LoggerFactory.getLogger(A_ProducerDemo.class.getSimpleName());

    public static void main(String[] args) {

        // Given
        // bin/kafka-server-start.sh config\kraft\my-server-10.properties
        // bin/kafka-server-start.sh config\kraft\my-server-20.properties
        // bin/kafka-server-start.sh config\kraft\my-server-30.properties
        // bin/kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic demo-java --partitions 1 --replication-factor 1
        // bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic demo-java

        // Create Producer properties
        Properties properties = new Properties();

        // Connect to localhost
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Set Producer properties
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // Create a Producer record
         ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo-java", "hello world");

        // Send data - asynchronous operation
        producer.send(producerRecord);

        // Flush and close the Producer - synchronous
        // Tell the producer to send all data and block until done -- synchronous
        // Otherwise the program main will finish before the send async task has been performed
        // No message sent in that case
        producer.flush();
        // Close the producer
        producer.close();

    }

}
