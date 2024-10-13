package com.github.alexwang.kafka.streams.stateful;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class StatefulTransformOperation {
    private final static Logger LOG = LoggerFactory.getLogger(StatefulTransformOperation.class);
    private final static String APP_ID = "stateful_transform_operation";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "input.words";
    private final static String TARGET_TOPIC = "output.words.count";
    private final static String STATE_STORE_NAME = "word-count";

    public static void main(String[] args) throws InterruptedException {
        final Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        properties.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);

        StoreBuilder<KeyValueStore<String, Integer>> keyValueStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(STATE_STORE_NAME), Serdes.String(), Serdes.Integer()
        );

        StreamsBuilder builder = new StreamsBuilder();
        builder.addStateStore(keyValueStoreBuilder);

        //<null, hello kafka >
        KStream<String, String> ks0 = builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), Serdes.String())
                .withName("source-processor")
                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        //<hello,hello>
        //<kafka,kafka>
        ks0.flatMap((k, v) -> Arrays.stream(v.split("\\s+")).map(e -> KeyValue.pair(e, e)).collect(Collectors.toList()),
                        Named.as("flat-map-processor"))
                .repartition(Repartitioned.with(Serdes.String(), Serdes.String()))
                .transform(() -> new Transformer<String, String, KeyValue<String, Integer>>() {
                    private KeyValueStore<String, Integer> keyValueStore;

                    @Override
                    public void init(ProcessorContext context) {
                        this.keyValueStore = context.getStateStore(STATE_STORE_NAME);
                    }

                    @Override
                    public KeyValue<String, Integer> transform(String key, String value) {
                        Integer count = keyValueStore.get(key);
                        if (count == null || count == 0) {
                            count = 1;
                        } else {
                            count++;
                        }
                        keyValueStore.put(key, count);
                        return KeyValue.pair(key, count);
                    }

                    @Override
                    public void close() {

                    }
                }, Named.as("stateful-transform-processor"), STATE_STORE_NAME)
                .peek((k, v) -> LOG.info("word:{},count:{}", k, v))
                .to(TARGET_TOPIC, Produced.with(Serdes.String(), Serdes.Integer()));

        final Topology topology = builder.build();
        final KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kafkaStreams.close();
            latch.countDown();
            LOG.info("The kafka streams application is graceful closed.");
        }));

        kafkaStreams.start();
        LOG.info("The kafka streams application start...");
        latch.await();
    }
}