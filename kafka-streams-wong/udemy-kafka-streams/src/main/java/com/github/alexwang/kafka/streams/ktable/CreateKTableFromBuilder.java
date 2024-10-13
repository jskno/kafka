package com.github.alexwang.kafka.streams.ktable;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class CreateKTableFromBuilder {
    private final static Logger LOG = LoggerFactory.getLogger(CreateKTableFromBuilder.class);
    private final static String APP_ID = "create_ktable_from_builder";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    //<alex,wang>
    private final static String SOURCE_TOPIC = "users";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        StreamsBuilder builder = new StreamsBuilder();
        /**
         * 1. input records with null key will be dropped.
         * 2. specified input topics must be partitioned by key
         * 3. The resulting KTable will be materialized in a local KeyValueStore with an internal store name. ?? X
         * 4. An internal changelog topic is created by default.  ??        X
         * 5. topology.optimization=all, the local state store and changelogs will not create, otherwise, the local statestore and remote change logs will be auto create??? X
         */
        /*builder.table(SOURCE_TOPIC, Consumed.with(Serdes.String(), Serdes.String()).withName("source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .toStream()
                .print(Printed.<String, String>toSysOut().withLabel("KT"));*/

        builder.table(SOURCE_TOPIC, Consumed.with(Serdes.String(), Serdes.String()).withName("source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST), Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as("users-state-store"))
                .toStream()
                .print(Printed.<String, String>toSysOut().withLabel("KT"));

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
