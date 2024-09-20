package com.jskno.producer;

import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class A_ProducerDemo_Docker {

    private static final Logger log = LoggerFactory.getLogger(A_ProducerDemo_Docker.class.getSimpleName());

    public static void main(String[] args) {

        // Given
        // bin/kafka-server-start.sh config\kraft\my-server-10.properties
        // bin/kafka-server-start.sh config\kraft\my-server-20.properties
        // bin/kafka-server-start.sh config\kraft\my-server-30.properties
        // bin/kafka-topics.sh --bootstrap-server 127.0.0.1:9092 --create --topic demo-java --partitions 1 --replication-factor 1
        // bin/kafka-console-consumer.sh --bootstrap-server 127.0.0.1:9092 --topic demo-java

        // create Producer properties
        Properties properties = new Properties();

        // connect to Secured KafkaBroker DockerCompose
        properties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin-secret\";");
        properties.setProperty("sasl.mechanism", "PLAIN");
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "kafka-broker:9092");

        // set Producer properties
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // create a Producer record
         ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo-java", "hello world");

        // send data - asynchronous operation
        producer.send(producerRecord);

        // flush and close the Producer - synchronous
        // tell the producer to send all data and block until done -- synchronous
        producer.flush();
        // close the producer
        producer.close();

    }

}
