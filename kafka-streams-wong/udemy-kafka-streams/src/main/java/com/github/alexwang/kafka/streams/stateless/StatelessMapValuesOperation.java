package com.github.alexwang.kafka.streams.stateless;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StatelessMapValuesOperation {
    private final static Logger LOG = LoggerFactory.getLogger(StatelessMapValuesOperation.class);
    private final static String APP_ID = "stateless_mapValues_operation";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "input.words";

    public static void main(String[] args) throws InterruptedException {
        final Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> ks0 = builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), Serdes.String()).withName("source-processor")
                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));
        KStream<String, String> ks1 = ks0.mapValues(v -> v.toUpperCase(), Named.as("map-values-processor"));
        KStream<String, String> ks2 = ks0.mapValues((k, v) -> (k + "---" + v).toUpperCase(), Named.as("map-values-withKey-processor"));
        ks1.print(Printed.<String, String>toSysOut().withLabel("mapValues"));
        ks2.print(Printed.<String, String>toSysOut().withLabel("mapValuesWithKey"));

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
