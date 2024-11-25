package com.jskno.a_basic;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicStreams {

    private static final Logger log = LoggerFactory.getLogger(BasicStreams.class);

    public static void main(String[] args) {
        Properties properties = buildStreamsProperties();
        Topology topology = buildTopology(properties);

        try(KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {
            final CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaStreams.close();
                latch.countDown();
                log.info("The kafka streams application is graceful closed.");
            }));

            TopicLoader.runProducer();
            try {
                kafkaStreams.start();
                log.info("The Kafka Streams application starts...");
                latch.await();
            } catch (Throwable e) {
                System.exit(1);
            }
        }
        System.exit(0);

    }


    private static Properties buildStreamsProperties() {
        Properties properties = new Properties();
        try(FileInputStream fis = new FileInputStream("src/main/resources/streams.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "basic-stream-app");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return properties;
    }

    private static Topology buildTopology(Properties properties) {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        final String inputTopic = properties.getProperty("basic.input.topic");
        final String outputTopic = properties.getProperty("basic.output.topic");

        final String orderNumberStart = "orderNumber-";

        KStream<String, String> sourceStream = streamsBuilder.stream(inputTopic, Consumed.with(Serdes.String(), Serdes.String()));

        sourceStream
            .peek((key, value) -> log.info("Incoming record - key " + key + " value " + value))
            .filter((key, value) -> value.contains(orderNumberStart))
            .mapValues(value -> value.substring(value.indexOf("-") + 1))
            .filter((key, value) -> Long.parseLong(value) > 1000)
            .peek((key, value) -> log.info("Outgoing record - key " + key + " value " + value))
            .to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));

        return streamsBuilder.build();
    }

}
