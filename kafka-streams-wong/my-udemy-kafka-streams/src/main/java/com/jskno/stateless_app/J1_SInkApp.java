package com.jskno.stateless_app;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-processor-input
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic map-values-target-default-serdes --property print.key=true --property print.value=true --property key.separator=: --from-beginning
public class J1_SInkApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(J1_SInkApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "sink-values-default-processor");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
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
                .collect(Collectors.toList()), Named.as("map-values-default"))
                .peek((k, v) -> LOGGER.info("MapValues Word Processor Key: " + k + " Value: " + v))
                .to("map-values-target-default-serdes");

        return builder.build();
    }
}
