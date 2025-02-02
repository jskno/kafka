package com.jskno.h_ktable;

import com.jskno.h_ktable.model.User;
import com.jskno.serdes.JsonSerdes;
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
public class B1_KTableFromKStreamApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(B1_KTableFromKStreamApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktabke-from-kstream-1");
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
        KTable<String, User> sourceStream = builder.stream(
                        "users",
                        Consumed.with(Serdes.String(), JsonSerdes.of(User.class)).
                                withName("source-processor")
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .selectKey((nullKey, value) -> value.getId())
                .toTable(Named.as("to-table-processor"),
                        Materialized.<String, User, KeyValueStore<Bytes, byte[]>>as("users-obj-materialized")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.of(User.class)));

        sourceStream.toStream()
                .print(Printed.<String, User>toSysOut().withLabel("KT"));

        return builder.build();
    }
}
