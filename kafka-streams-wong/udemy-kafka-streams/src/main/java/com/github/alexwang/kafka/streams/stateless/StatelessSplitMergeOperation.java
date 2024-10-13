package com.github.alexwang.kafka.streams.stateless;

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

public class StatelessSplitMergeOperation {
    private final static Logger LOG = LoggerFactory.getLogger(StatelessSplitMergeOperation.class);
    private final static String APP_ID = "stateless_split_merge_operation";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "input.words";

    public static void main(String[] args) throws InterruptedException {
        final Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> ks0 = builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), Serdes.String())
                .withName("source-processor")
                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        //split-stream-apache
        //split-stream-kafka
        //split-stream-streams
        //split-stream-default
        Map<String, KStream<String, String>> kStreamMap = ks0.split(Named.as("split-stream-"))
                .branch((k, v) -> v.contains("apache"), Branched.as("apache"))
                .branch((k, v) -> v.contains("kafka"), Branched.as("kafka"))
                .branch((k, v) -> v.contains("streams"), Branched.withFunction(ks -> ks.mapValues(e -> e.toUpperCase()), "streams"))
                .defaultBranch(Branched.as("default"));

        kStreamMap.get("split-stream-apache").print(Printed.<String, String>toSysOut().withLabel("apache"));
        kStreamMap.get("split-stream-kafka").print(Printed.<String, String>toSysOut().withLabel("kafka"));
        kStreamMap.get("split-stream-streams").print(Printed.<String, String>toSysOut().withLabel("streams"));
        kStreamMap.get("split-stream-default").print(Printed.<String, String>toSysOut().withLabel("default"));

        kStreamMap.get("split-stream-streams").merge(kStreamMap.get("split-stream-default"), Named.as("merge-processor"))
                .print(Printed.<String, String>toSysOut().withLabel("merged"));

        final Topology topology = builder.build();
        final KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kafkaStreams.close();
            latch.countDown();
            LOG.info("The kafka streams application is graceful closed.");
        }));

        kafkaStreams.start();
        LOG.info("The kafka streams application start...");
        latch.await();
    }
}
