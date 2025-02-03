package com.jskno.h_ktable;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic user-info --partitions 3
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic user-address --partitions 3
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic employee

// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic user-info --property parse.key=true --property key.separator=:
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic user-address --property parse.key=true --property key.separator=:
public class G_KTableJoinKTableApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(G_KTableJoinKTableApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktable-inner-join-ktable-operations");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, String> userKtable = builder.table(
                "user-info",
                Consumed.with(Serdes.String(), Serdes.String())
                        .withName("user-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST),
                Materialized.as("user-info-materialized-ktable"));

        KTable<String, String> addressTable = builder.table(
                "user-address",
                Consumed.with(Serdes.String(), Serdes.String())
                        .withName("address-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST),
                Materialized.as("user-address-materialized-ktable"));

        userKtable.join(addressTable, (username, address) -> String.format("%s comes from :%s", username, address))
                .toStream()
                .print(Printed.<String, String>toSysOut().withLabel("ktable-inner-join-ktable"));

        return builder.build();
    }
}
