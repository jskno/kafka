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

public class C_ProducerDemo_WithCallbackLoop {

    public static final Logger log = LoggerFactory.getLogger(C_ProducerDemo_WithCallbackLoop.class.getSimpleName());
    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for(int i = 0; i < 10; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("demo-java", "record " + i);
            producer.send(producerRecord, (metadata, ex) -> {
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
        }

        producer.flush();
        producer.close();

        // All the messages will go to the same partition.
        // Producer is using StickyPartitioner because as we are sending 10 messages quite fast, the producer will group them
        // and send them together to increase performance (batching)
    }

}
