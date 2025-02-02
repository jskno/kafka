package com.jskno.h_ktable;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic users --partitions 3
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic users
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic users --property parse.key=true --property key.separator=:
public class A_KTableFromStreamBuilderApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(A_KTableFromStreamBuilderApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktabke-from-stream-builder");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Users event example:
        // user1:{"id":"user1",name:"Jose Cano","age":48}

        /**
         * 1. input records with null key will be dropped.
         * 2. specified input topics must be partitioned by key.
         * 3. The resulting KTable will be materialized in a local KeyValueStore with an internal store name. ?? X
         * 4. An internal changelog topic is created by default.  ??        X
         * 5. topology.optimization=all, the local state store and changelogs will not create,
         *          otherwise, the local statestore and remote change logs will be auto create??? X
         */
        /*
        KTable<String, String> sourceStream = builder.table(
                "users",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));
         */

        // For points 3 and 4 to happen, we need to add Materialized.as (line 76)
        KTable<String, String> sourceStream = builder.table(
                "users",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST),
                Materialized.as("users-materialized"));

        sourceStream.toStream()
                .print(Printed.<String, String>toSysOut().withLabel("KT"));

        return builder.build();
    }
}
