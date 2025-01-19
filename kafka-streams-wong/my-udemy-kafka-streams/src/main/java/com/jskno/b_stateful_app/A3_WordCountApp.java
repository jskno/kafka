package com.jskno.b_stateful_app;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
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
// Two topics previously created:
//              words-count-output
//              words-input

// Two internal topics created by the KakfaStreams app:
//      stateful-app-KSTREAM-REPARTITION-0000000002-repartition --> APP_ID Repartition topic, very importante because the 3 tppic will have 3 partitions and the topic will be assigned according to its new key (hello, kafka, scala) so that each task will process from same topic and the state store will store all values from same keys
//      stateful-app-a3-words-store-changelog --> APPLICATION_ID + STATE_STORE_NAME + -changelog (stateful-app)+(a3-words-store)+-changelog


// 6. Produce some records in the topics
/*
./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic words-input
    hello kafka
    hello streams
    hello apache
    hello world

 */

// 7. Consume at the same time from the output topic
/*
./bin/kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic words-count-output \
--property print.key=true \
--property print.value=true \
--property key.separator=: \
--from-beginning
 */
public class A3_WordCountApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(A3_WordCountApp.class);
    public final static String STATE_STORE_NAME = "a3-words-store";

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
                Stores.persistentKeyValueStore(STATE_STORE_NAME), Serdes.String(), Serdes.Integer());
        StreamsBuilder builder = new StreamsBuilder();

        builder.addStateStore(keyValueStoreBuilder);

        // (null, "hello kafka")
        KStream<String, String> sourceStream = builder.stream(
                "words-input",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        // ("hello", "hello")
        // ("kafka", "kafka")
        sourceStream
                .flatMap((k, v) ->
                                Arrays.stream(v.split("\\s+")).map(e -> KeyValue.pair(e, e)).collect(Collectors.toList()),
                        Named.as("flat-map-op"))
                .repartition(Repartitioned.with(Serdes.String(), Serdes.String()))
                .process(() -> new Processor<String, String, String, String>() {

                    private KeyValueStore<String, Integer> store;
                    private ProcessorContext<String, String> context;

                    @Override
                    public void init(ProcessorContext<String, String> context) {
                        this.context = context;
                        store = context.getStateStore(STATE_STORE_NAME);
                        //Processor.super.init(context);
                    }

                    @Override
                    public void process(Record<String, String> record) {
                        Integer count = store.get(record.key());
                        if (count == null) {
                            count = 1;
                        } else {
                            count++;
                        }
                        store.put(record.key(), count);
                        context.forward(new Record<>(record.key(), count.toString(), record.timestamp()));
                    }

                    @Override
                    public void close() {
                        //Processor.super.close();
                    }


                        }, Named.as("stateful-transform-processor"), STATE_STORE_NAME)
                .peek((k, v) -> LOGGER.info("Word: {} Count: {}", k, v))
                .to("words-count-output", Produced.with(Serdes.String(), Serdes.String()));


        return builder.build();
    }
}
