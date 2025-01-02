package com.jskno.stateless.app;

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

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-processor-input
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic word-processor-output --from-beginning
public class A_WordProcessorApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(A_WordProcessorApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-processor");
        //props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> sourceStream = builder.stream(
                        "word-processor-input",
                        Consumed.with(Serdes.String(), Serdes.String())
                                .withName("word-source-processor")
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST)
                )
                .peek((k, v) -> LOGGER.info("Source Record --> key: {}, value: {}", k, v), Named.as("pre-word-transform"));

        sourceStream
                .filter((k, v) -> v != null && v.length() > 5, Named.as("filter-word-transform"))
                .mapValues(v -> v.toUpperCase(), Named.as("map-word-transform"))
                .peek((k, v) -> LOGGER.info("Transformed Record --> key: {}, value: {}", k, v))
                .to("word-processor-output",
                        Produced.with(Serdes.String(), Serdes.String()).withName("word-processor-output"));

        return builder.build();
    }
}
