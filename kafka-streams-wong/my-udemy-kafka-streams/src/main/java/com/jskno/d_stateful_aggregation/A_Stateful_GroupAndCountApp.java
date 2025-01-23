package com.jskno.d_stateful_aggregation;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.DslKeyValueParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-processor-input
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic word-processor-output --from-beginning
public class A_Stateful_GroupAndCountApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(A_Stateful_GroupAndCountApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "count-app");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);

        // These two props control how often the stream task is entirely consume and process until final operation its topology
        //props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);  // deprecated
        //props.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        //props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // <null, This is Kafka Stream>
        // <null, I like Kafka Stream>
        KStream<String, String> sourceStream = builder.stream(
                "input-words",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("word-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        sourceStream
                .flatMap((k, v) -> Arrays.stream(v.split("\\s+"))
                        .map(e -> KeyValue.pair(e, 1L))
                        .collect(Collectors.toList()), Named.as("flatmap-words"))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .count(Named.as("words-count"), Materialized.as("words-count"))
                .toStream()
                .print(Printed.<String, Long>toSysOut().withLabel("wc"));

        return builder.build();
    }
}
