package com.jskno.h_ktable;

import com.jskno.h_ktable.model.Shoot;
import com.jskno.h_ktable.model.ShootStats;
import com.jskno.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic shoot-game --partitions 3
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic shoot-game

// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic shoot-game --property parse.key=true --property key.separator=:
public class D_KTableTransformValuesApiApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(D_KTableTransformValuesApiApp.class);
    private final static String STATE_STORE_NAME = "shoot-state-store";

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktabke-process-api");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }

    private static Topology buildTopology() {
        StoreBuilder<KeyValueStore<String, ShootStats>> storeBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(STATE_STORE_NAME), Serdes.String(), JsonSerdes.of(ShootStats.class));

        StreamsBuilder builder = new StreamsBuilder();
        builder.addStateStore(storeBuilder);

        // Employee event example:
        KTable<String, Shoot> kTable = builder.table(
                "shoot-game",
                Consumed.with(Serdes.String(), JsonSerdes.of(Shoot.class)).
                        withName("source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        kTable.transformValues(() -> new ValueTransformerWithKey<String, Shoot, ShootStats>() {

                    private KeyValueStore<String, ShootStats> keyValueStore;

                    @Override
                    public void init(ProcessorContext processorContext) {
                        keyValueStore = processorContext.getStateStore(STATE_STORE_NAME);
                    }

                    @Override
                    public ShootStats transform(String readOnlyKey, Shoot shoot) {
                        LOGGER.info("Key: {}, Value: {}", readOnlyKey, shoot);
                        // Tombstone record handle
                        if (shoot == null) {
                            keyValueStore.delete(readOnlyKey);
                            return null;
                        }
                        ShootStats shootStats = keyValueStore.get(readOnlyKey);
                        if (shootStats == null) {
                            shootStats = ShootStats.newBuilder(shoot).build();
                        } else if (shootStats.getStatus().equalsIgnoreCase("FINISHED")) {
                            return shootStats;
                        } else if (shootStats.getCount() == 10) {
                            shootStats.setStatus("FINISHED");
                        } else {
                            shootStats.setCount(shootStats.getCount() + 1);
                            shootStats.setLastScore(shoot.getScore());
                            shootStats.setBestScore(Math.max(shoot.getScore(), shootStats.getBestScore()));
                        }
                        keyValueStore.put(readOnlyKey, shootStats);
                        return shootStats;
                    }

                    @Override
                    public void close() {

                    }
                }, STATE_STORE_NAME)
                .filter((k, v) -> v.getBestScore() > 8)
                .filterNot((k,v) -> v.getStatus().equals("FINISHED"))
                .toStream()
                .filter((k,v) -> v != null)
                .print(Printed.<String, ShootStats>toSysOut().withLabel("shoot-game"));

        return builder.build();
    }
}
