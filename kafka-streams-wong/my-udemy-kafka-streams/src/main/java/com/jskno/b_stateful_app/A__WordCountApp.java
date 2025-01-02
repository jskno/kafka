package com.jskno.b_stateful_app;

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


// 1. Start the server
// sudo ./bin/kafka-server-start.sh config/kraft/server.properties

// 2. Create the two topics we need
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic words-input --partitions 3 --replication-factor 1
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic words-count-output --partitions 3 --replication-factor 1

// 3. Check the topics
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

// 4. Run the app
// 5. Check the topics again and the kafka/statestore folder structure
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
//              stateful_word-processor-stateful-transform-store-changelog
//              APPLICATION_ID + STATE_STORE_NAME + -changelog
//              words-count-output
//              words-input

// 6. Produce some records in the topics
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic words-input
//      hello kafka
//      hello streams
//      hello apache
//      hello world

// 7. Consume at the same time from the output topic
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic words-count-output --from-beginning
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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "stateful-app");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "./kafka/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
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
