package com.jskno.stateless_app;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-source-processor --property key.separator=: --property parse.key=true
public class C_MapValuesKeyValueMapperApp {

    public static void main(String[] args) throws InterruptedException {

        Properties pros = buildStreamsProperties();
        Topology topology = buildTopology();

        KafkaStreams streams = new KafkaStreams(topology, pros);

        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
        }));

        streams.start();
        latch.await();
    }

    private static Properties buildStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "mapValuesApplication");
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> sourceStream = builder.stream(
                "word-source-processor",
                Consumed.with(Serdes.String(), Serdes.String())
                        .withName("word-map-values-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        KStream<String, String> mapValues = sourceStream
                .mapValues(value -> value.toUpperCase(), Named.as("map-values-processor"));
        KStream<String, String> mapValuesWithKeys = sourceStream
                .mapValues((k, v) -> (k + "---" + v).toUpperCase(), Named.as("map-values-with-key-processor"));

        mapValues.print(Printed.<String, String>toSysOut().withLabel("mapValues"));
        mapValuesWithKeys.print(Printed.<String, String>toSysOut().withLabel("mapValuesWithKeys"));

        return builder.build();
    }

}
