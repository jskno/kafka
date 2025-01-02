package com.jskno.a_stateless_app;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-processor-input
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic map-values-target --property print.key=true --property print.value=true --from-beginning
public class J0_SInkApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(J0_SInkApp.class);

    public static void main(String[] args) throws InterruptedException {
        Properties props = buildStreamsProperties();
        Topology topology = buildTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);

        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
            LOGGER.info("The WordProcessorApp is gracefully shutting down");
        }));

        streams.start();
        LOGGER.info("WordProcessorApp is started");

        latch.await();

    }

    private static Properties buildStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sink-values-processor");
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> sourceStream = builder.stream(
                "word-processor-input",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("word-processor-input")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));

        sourceStream
                .flatMapValues(v -> Arrays.stream(v.split("\\s+"))
                .map(String::toUpperCase)
                .collect(Collectors.toList()), Named.as("map-values"))
                .peek((k, v) -> LOGGER.info("MapValues Word Processor Key: " + k + " Value: " + v))
                .to("map-values-target",
                        Produced.with(Serdes.String(), Serdes.String()).withName("map-values-target"));

        return builder.build();
    }
}
