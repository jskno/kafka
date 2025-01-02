package com.jskno.stateless.app;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-processor-input
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic map-word-output --from-beginning
public class B_MapKeyVaueMapperApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(B_MapKeyVaueMapperApp.class);

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
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> sourceStream = builder.stream(
                        "word-processor-input",
                        Consumed.with(Serdes.String(), Serdes.String())
                                .withName("map-source-processor")
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST)
                )
                .peek((k, v) -> LOGGER.info("Source Record --> key: {}, value: {}", k, v), Named.as("pre-word-transform"));

        KStream<String, Integer> mapProcessor = sourceStream
                .map((noKey, v) -> KeyValue.pair(v, v.length()), Named.as("map-word-processor"));
        mapProcessor.to("map-word-output", Produced.with(Serdes.String(), Serdes.Integer()));

        mapProcessor.print(Printed.<String, Integer>toSysOut().withName("map"));

        return builder.build();
    }
}
