package com.jskno.b_stateful_app;

import com.jskno.b_stateful_app.processor.CustomProcessor;
import com.jskno.b_stateful_app.processor.CustomProcessorSupplier;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;


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
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic words-count-output --property key.separator=: --property print.key=true --property print.value=true --from-beginning
public class A2_WordCountApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(A2_WordCountApp.class);
    public final static String STATE_STORE_NAME = "a2-words-store";

    public static void main(String[] args) throws InterruptedException {
        Properties props = buildStreamsProperties();
        Topology topology = buildTopology();

        try(KafkaStreams streams = new KafkaStreams(topology, props)) {

            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                streams.close();
                latch.countDown();
                LOGGER.info("The WordProcessorApp is gracefully shutting down");
            }));

            try {
                streams.start();
                LOGGER.info("WordProcessorApp is started");
                latch.await();
            } catch (Throwable e) {
                System.exit(1);
            }
        }
        System.exit(0);

    }

    private static Properties buildStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "a2-stateful-app");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "./kafka/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }

    private static Topology buildTopology() {

        StreamsBuilder builder = new StreamsBuilder();

        Topology topology = builder.build();
        topology.addSource(
                Topology.AutoOffsetReset.LATEST,
                "a2-word-count-op",
                Serdes.String().deserializer(),
                Serdes.String().deserializer(),
                "words-input");

        topology.addProcessor(
                "a2-custom-processor",
                new CustomProcessorSupplier(STATE_STORE_NAME),
                "a2-word-count-op");

        topology.addSink(
                "a2-words-output",
                "words-count-output",
                Serdes.String().serializer(),
                Serdes.Integer().serializer(),
                "a2-custom-processor");

        return topology;
    }
}
