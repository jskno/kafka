package com.jskno.f_complex_streams;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/*
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic time-default --partitions 3 --replication-factor 1
./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic time-append \
--partitions 3 --replication-factor 1 \
--config message.timestamp.type=LogAppendTime
 */

@Slf4j
public class A0_ProducerClient {

    private static final String TIME_DEFAULT_TOPIC = "time-default";
    private static final String TIME_APPEND_TOPIC = "time-append";

    public static void main(String[] args) {
        Properties props = buildProperties();
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            long currentTimeMillis = System.currentTimeMillis();
            log.info("********************************");
            log.info("Current time is {}", currentTimeMillis); // 1737922410832
            log.info("********************************");

            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(
                    "time-default", null, currentTimeMillis, null, "test-time-default");
            ProducerRecord<String, String> producerRecord2 = new ProducerRecord<>(
                    "time-append", null, currentTimeMillis, null, "test-time-append");


            Future<RecordMetadata> send = producer.send(producerRecord, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error sending record", exception);
                } else {
                    log.info("Sent record {}", producerRecord);
                }
            });
            send.get();

            Future<RecordMetadata> send2 = producer.send(producerRecord2, (metadata, exception) -> {
                if (exception != null) {
                    log.error("Error sending record", exception);
                } else {
                    log.info("Sent record {}", producerRecord);
                }
            });
            send2.get();

            producer.flush();

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

    }



    private static Properties buildProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }
}
