package com.jskno.producer;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class B_ProducerDemo_WithCallback {

    public static final Logger log = LoggerFactory.getLogger(B_ProducerDemo_WithCallback.class.getSimpleName());
    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static void main(String[] args) {

        log.info("I am a Kafka Producer with Callback !!");

        // Create Producer Properties
        Properties properties = new Properties();

        // Connect to localhost
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Set Producer Properties
        properties.setProperty(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create the Producer
         KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

         // Create a Producer record
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo-java", "hello world");

        // Send data - asynchronous operation
        producer.send(producerRecord, (metadata, ex) -> {
            // executes every time a record is successfully sent or an exception is thrown
            if (ex == null) {
                StringBuilder builder = new StringBuilder();
                builder.append("Received new metadata").append(LINE_SEPARATOR);
                builder.append("Topic: ").append(metadata.topic()).append(LINE_SEPARATOR);
                builder.append("Partition: ").append(metadata.partition()).append(LINE_SEPARATOR);
                builder.append("Offset: ").append(metadata.offset()).append(LINE_SEPARATOR);
                builder.append("Timestamp: ").append(metadata.timestamp()).append(LINE_SEPARATOR);
                log.info(builder.toString());
            } else {
                log.error("Error while producing", ex);
            }
        });

        // Flush and close the Producer - synchronous
        producer.flush();
        producer.close();

        // All the messages will go to different partition.
        // Producer is using RoundRobin partition the producer will alternate between partition in an equally way
    }

}
