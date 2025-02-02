package com.jskno.h_ktable;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic users --partitions 3
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic users

// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic users --property parse.key=true --property key.separator=:
public class B0_KTableFromKStreamApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(B0_KTableFromKStreamApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktabke-from-kstream-0");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // We need to build a KTable from a KStream instead from a StreamBuilder when
        // some additional operations is needed to be done before defining the table.
        // Foe example let's assume we don't have a key in the original event
        // we first need first to select a key from the message to be able to create the table

        // Users event example:
        // null:{"id":"user1",name:"Jose Cano","age":48}
        KTable<String, String> sourceStream = builder.stream(
                        "users",
                        Consumed.with(Serdes.String(), Serdes.String()).
                                withName("source-processor")
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .map((nullKey, value) -> KeyValue.pair(value.split(",")[0], value))
                .toTable(Named.as("to-string-table-processor"),
                        Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("string-users-materialized")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.String()));

        sourceStream.toStream()
                .print(Printed.<String, String>toSysOut().withLabel("KT"));

        return builder.build();
    }
}
