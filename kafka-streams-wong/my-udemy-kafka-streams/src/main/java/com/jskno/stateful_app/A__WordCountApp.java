package com.jskno.stateful_app;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-processor-input
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic word-processor-output --from-beginning
public class A__WordCountApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(A__WordCountApp.class);
    private final static String STATE_STORE_NAME = "stateful-transform-store";

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stateful_word-processor");
        return props;
    }

    private static Topology buildTopology() {
        StoreBuilder<KeyValueStore<String, Integer>> keyValueStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(STATE_STORE_NAME), Serdes.String(), Serdes.Integer()
        );
        StreamsBuilder builder = new StreamsBuilder();
        builder.addStateStore(keyValueStoreBuilder);

        // (null, "hello kafka")
        KStream<String, String> sourceStream = builder.stream(
                "words-input",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("stateful-word-processor-input")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        // ("hello", "hello")
        // ("kafka", "kafka")
        sourceStream
                .flatMap((k, v) ->
                                Arrays.stream(v.split("\\s+")).map(e -> KeyValue.pair(e, e.length())).collect(Collectors.toList()),
                        Named.as("flat-sentence-op"))
                .process(() -> new Processor<String, Integer, String, Integer>() {

                            private KeyValueStore<String, Integer> store;

                            @Override
                            public void init(ProcessorContext<String, Integer> context) {
                                store = context.getStateStore(STATE_STORE_NAME);
                                Processor.super.init(context);
                            }

                            @Override
                            public void process(Record<String, Integer> record) {
                                Integer count = store.get(record.key());
                                if (count == null) {
                                    count = 1;
                                } else {
                                    count++;
                                }
                                store.put(record.key(), count);
                            }

                            @Override
                            public void close() {
                                Processor.super.close();
                            }
                        }, Named.as("stateful-transform-processor"), STATE_STORE_NAME)
                .peek((k, v) -> LOGGER.info("Key: " + k + " Value: " + v))
                .to("words-count-output", Produced.with(Serdes.String(), Serdes.Integer()));

        return builder.build();
    }
}
