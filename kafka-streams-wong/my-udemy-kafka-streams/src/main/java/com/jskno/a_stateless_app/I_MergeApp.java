package com.jskno.a_stateless_app;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-processor-input
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic word-processor-output --from-beginning
public class I_MergeApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(I_MergeApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "merge-processor");
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> sourceStream = builder.stream(
                "word-processor-input",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("word-processor-input")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));

        // split-stream-apache
        // split-stream-kafka
        // split-stream-streams
        // split-stream-default
        // NOTE: The event with value "kafka apache streams" will go only to the apache branch
        Map<String, KStream<String, String>> kStreamMap = sourceStream
                .split(Named.as("split-stream-"))
                .branch((k, v) -> v.contains("apache"), Branched.as("apache"))
                .branch((k, v) -> v.contains("kafka"), Branched.as("kafka"))
                .branch((k, v) -> v.contains("streams"),
                        Branched.withFunction(ks -> ks.mapValues(e -> e.toUpperCase()), "streams"))
                .defaultBranch(Branched.as("default"));

        kStreamMap.get("split-stream-apache").print(Printed.<String, String>toSysOut().withLabel("apache"));
        kStreamMap.get("split-stream-kafka").print(Printed.<String, String>toSysOut().withLabel("kafka"));
        kStreamMap.get("split-stream-streams").print(Printed.<String, String>toSysOut().withLabel("streams"));
        kStreamMap.get("split-stream-default").print(Printed.<String, String>toSysOut().withLabel("default"));

        kStreamMap.get("split-stream-streams")
                .merge(kStreamMap.get("split-stream-default"), Named.as("merge-processor"))
                .print(Printed.<String, String>toSysOut().withLabel("merged"));


        return builder.build();
    }
}
