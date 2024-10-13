package com.github.alexwang.kafka.streams.ktable;

import com.github.alexwang.kafka.streams.model.Shoot;
import com.github.alexwang.kafka.streams.model.ShootStats;
import com.github.alexwang.kafka.streams.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * for shoot game
 */
public class KTableTransformValuesOperationApp {
    private final static Logger LOG = LoggerFactory.getLogger(KTableTransformValuesOperationApp.class);
    private final static String APP_ID = "ktable_transform_values_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "shoot.game";
    private final static String STATE_STORE = "shoot_state_store";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        StreamsBuilder builder = new StreamsBuilder();

        StoreBuilder<KeyValueStore<String, ShootStats>> storeBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(STATE_STORE), Serdes.String(), JsonSerdes.ShootStatsSerde()
        );
        builder.addStateStore(storeBuilder);
        KTable<String, Shoot> kTable = builder.table(SOURCE_TOPIC, Consumed.with(Serdes.String(), JsonSerdes.ShootSerde()).withName("source")
                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        kTable.transformValues(() -> new ValueTransformerWithKey<String, Shoot, ShootStats>() {
                    private KeyValueStore<String, ShootStats> keyValueStore;

                    @Override
                    public void init(ProcessorContext context) {
                        this.keyValueStore = context.getStateStore(STATE_STORE);
                    }

                    @Override
                    public ShootStats transform(String readOnlyKey, Shoot value) {
                        LOG.info("key:{},value:{}", readOnlyKey, value);
                        //tombstone records handle
                        if (value == null) {
                            this.keyValueStore.delete(readOnlyKey);
                            return null;
                        }
                        ShootStats shootStats = keyValueStore.get(readOnlyKey);
                        if (shootStats == null) {
                            shootStats = ShootStats.newBuilder(value).build();
                        } else if (shootStats.getStatus().equalsIgnoreCase("FINISHED")) {
                            return shootStats;
                        } else if (shootStats.getCount() == 10) {
                            shootStats.setStatus("FINISHED");
                        } else {
                            shootStats.setCount(shootStats.getCount() + 1);
                            shootStats.setLastScore(value.getScore());
                            shootStats.setBestScore(Math.max(value.getScore(), shootStats.getBestScore()));
                        }
                        this.keyValueStore.put(readOnlyKey, shootStats);
                        return shootStats;
                    }

                    @Override
                    public void close() {

                    }
                }, STATE_STORE).filter((k, v) -> v.getBestScore() >= 8)
                .filterNot((k, v) -> v.getStatus().equals("FINISHED"))
                .toStream()
                .filter((k, v) -> v != null)
                .print(Printed.<String, ShootStats>toSysOut().withLabel("shoot-game"));


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
