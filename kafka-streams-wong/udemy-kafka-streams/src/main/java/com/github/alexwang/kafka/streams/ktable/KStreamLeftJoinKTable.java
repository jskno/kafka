package com.github.alexwang.kafka.streams.ktable;

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

public class KStreamLeftJoinKTable {
    private final static Logger LOG = LoggerFactory.getLogger(KStreamLeftJoinKTable.class);
    private final static String APP_ID = "kstream_left_join_ktable";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String USER_INFO_TOPIC = "user.info";
    private final static String USER_ADDRESS_TOPIC = "user.address";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> stream = builder.stream(USER_INFO_TOPIC, Consumed.with(Serdes.String(), Serdes.String())
                .withName("user-source").withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        KTable<String, String> table = builder.table(USER_ADDRESS_TOPIC, Consumed.with(Serdes.String(), Serdes.String())
                .withName("address-source").withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST), Materialized.as("user-address"));

        KStream<String, String> joinedKStream = stream.leftJoin(table, (username, address) -> String.format("%s come from %s", username, address),
                Joined.with(Serdes.String(), Serdes.String(), Serdes.String()));

        joinedKStream.print(Printed.<String, String>toSysOut().withLabel("kstream-left-join-ktable"));

        Topology topology = builder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, conf);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kafkaStreams.close();
            latch.countDown();
            LOG.info("The kafka streams application is closed.");
        }));
        kafkaStreams.start();
        LOG.info("The kafka streams application start...");
        latch.await();
    }
}
