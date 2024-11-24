package com.jskno.a_basic;

import com.jskno.StreamsUtils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicLoader {

    private static final Logger log = LoggerFactory.getLogger(TopicLoader.class);

    public static void main(String[] args) {
        runProducer();
    }

    public static void runProducer() {
        Properties properties = buildProducerProperties();

        try (Admin adminClient = Admin.create(properties);
            KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties)) {

            final String inputTopic = properties.getProperty("basic.input.topic");
            final String outputTopic = properties.getProperty("basic.output.topic");
            var topics = List.of(StreamsUtils.createTopic(inputTopic), StreamsUtils.createTopic(outputTopic));
            adminClient.createTopics(topics);

            Callback callback = (recordMetadata, exception) -> {
                if (exception != null) {
                    log.error("Producing records encountered error {}", exception.getMessage(), exception);
                } else {
                    log.info("Record produced - offset - {} timestamp - {}", recordMetadata.offset(), recordMetadata.timestamp());
                }
            };

            var rawRecords = List.of(
                "orderNumber-1001",
                "orderNumber-5000",
                "orderNumber-999",
                "orderNumber-3330",
                "bogus-1",
                "bogus-2",
                "orderNumber-8400");
            var producerRecords = rawRecords.stream()
                .map(r -> new ProducerRecord<>(inputTopic,"order-key", r))
                .toList();
            producerRecords.forEach((pr -> kafkaProducer.send(pr, callback)));

        }
    }

    private static Properties buildProducerProperties() {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream("src/main/resources/streams.properties")) {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }

}
