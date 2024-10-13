package com.github.alexwang.kafka.streams.clients;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerClient {
    private final static Logger LOG = LoggerFactory.getLogger(ProducerClient.class);
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String TOPIC = "time.append";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties conf = new Properties();
        conf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(ProducerConfig.ACKS_CONFIG, "all");
        conf.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        conf.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(conf);
        long currentTime = System.currentTimeMillis();
        LOG.info("*********** event time:{}", currentTime);
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC, null, currentTime, null, "test data");

        Future<RecordMetadata> future = producer.send(producerRecord, (metadata, exception) -> {
            if (exception != null)
                LOG.info("The record is sent:{}", metadata);
        });
        producer.flush();
        future.get();
        producer.close();
    }
}
