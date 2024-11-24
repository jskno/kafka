package com.jskno.b_ktable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KTableExample {

    private static final Logger log = LoggerFactory.getLogger(KTableExample.class);

    public static void main(String[] args) {
        Properties properties = buildStreamsProperties();
        Topology topology = buildTopology(properties);

        try(KafkaStreams streams = new KafkaStreams(topology, properties)) {

            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                streams.close();
                latch.countDown();
                log.info("The kafka streams application is graceful closed.");
            }));

            TopicLoader.runProducer();
            try {
                streams.start();
                log.info("The Kafka Streams application starts...");
                latch.await();
            } catch (Throwable ex) {
                System.exit(1);
            }
        }
        System.exit(0);
    }

    private static Properties buildStreamsProperties() {
        Properties properties = new Properties();
        try(InputStream is = new FileInputStream("src/main/resources/streams.properties")) {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktable-app");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }


    private static Topology buildTopology(Properties properties) {
        StreamsBuilder builder = new StreamsBuilder();

        final String orderNumberStart = "orderNumber-";
        final String inputTopic = properties.getProperty("basic.input.topic");
        final String outputTopic = properties.getProperty("basic.output.topic");

        KTable<String, String> table = builder.table(inputTopic,
            Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("ktable-store")
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.String()));
        table.filter((key, value) -> value.contains(orderNumberStart))
            .mapValues(value -> value.substring(value.indexOf("-") + 1))
            .filter((key, value) -> Long.parseLong(value) > 1000)
            .toStream()
            .peek((key, value) -> log.info("Outgoing record - key " + key + " value " + value))
            .to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();
    }

}
