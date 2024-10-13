package com.github.alexwang.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class FirstStreamsApplication {
    //1. define variable
    private final static Logger LOG = LoggerFactory.getLogger(FirstStreamsApplication.class);
    private final static String APP_ID = "first_streams_app_id";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "input.words";
    private final static String TARGET_TOPIC = "output.words";

    public static void main(String[] args) throws InterruptedException {
        //2. create configuration
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);

        //3. create SteamsBuilder
        StreamsBuilder builder = new StreamsBuilder();

        builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), Serdes.String()).withName("source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .peek((k, v) -> LOG.info("[source] records: {}", v), Named.as("pre-transform-peek"))
                .filter((k, v) -> v != null && v.length() > 5, Named.as("filter-processor"))
                .mapValues(v -> v.toUpperCase(), Named.as("transform-processor"))
                .peek((k, v) -> LOG.info("[transform] records: {}", v), Named.as("post-transform-peek"))
                .to(TARGET_TOPIC, Produced.with(Serdes.String(), Serdes.String()).withName("sink-processor"));

        //4. create topology
        Topology topology = builder.build();

        //5. create kafka streams
        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kafkaStreams.close();
            latch.countDown();
            LOG.info("The kafka streams application is graceful shutdown...");
        }));

        //6. start the kafka streams
        kafkaStreams.start();
        LOG.info("The kafka streams application is started...");

        //7. wait for graceful shutdown
        latch.await();
    }
}
