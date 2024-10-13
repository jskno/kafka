package com.github.alexwang.kafka.streams.stateful;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StatefulLeftJoinOperation {
    private final static Logger LOG = LoggerFactory.getLogger(StatefulLeftJoinOperation.class);
    private final static String APP_ID = "stateful_left_join_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";

    //<null->001,Alex>
    private final static String USER_INFO_TOPIC = "user.info";
    //<null->001,CN>
    private final static String USER_ADDRESS_TOPIC = "user.address";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);

        StreamsBuilder builder = new StreamsBuilder();
        //<null->001,Alex>
        KStream<String, String> ksUser = builder.stream(USER_INFO_TOPIC, Consumed.with(Serdes.String(), Serdes.String())
                .withName("source-user-info").withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        //<null->001,CN>
        KStream<String, String> ksAddress = builder.stream(USER_ADDRESS_TOPIC, Consumed.with(Serdes.String(), Serdes.String())
                .withName("source-user-address").withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        //<001->001,Alex>
        KStream<String, String> ksUserWithKey = ksUser.map((k, v) -> KeyValue.pair(v.split(",")[0], v), Named.as("user-info-selectKey"));
        //<001->001,CN>
        KStream<String, String> ksAddressWithKey = ksAddress.map((k, v) -> KeyValue.pair(v.split(",")[0], v), Named.as("user-address-selectKey"));

        //1. auto trigger upstreams data redistrubution
        //2. auto the statestore(time to live)
        ksUserWithKey.leftJoin(ksAddressWithKey, (left, right) -> left + "---" + right, JoinWindows.of(Duration.ofMinutes(1)),
                        StreamJoined.with(Serdes.String(), Serdes.String(), Serdes.String()))
                .print(Printed.<String, String>toSysOut().withLabel("left-join"));

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
