package com.jskno.c_stateful_join_app;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

// 1. Start the server
//      sudo ./bin/kafka-server-start.sh config/kraft/server.properties

// 2. Create the two topics we need
//      ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic user-info --partitions 3 --replication-factor 1
//      ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic user-address --partitions 3 --replication-factor 1

// 3. Check the topics
//      ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

// 4. Run the app

// 5. Check the topics again and the kafka/statestore folder structure
//      ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

// 6. Produce some records in the topics
/*
./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic user-info \
--property key.separator=: --property parse.key=true

./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic user-address \
--property key.separator=: --property parse.key=true

 */

// 7. Consume at the same time from the output topic
/*
 ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic user-info \
 --property key.separator=: --property print.key=true \
 --property print.value=true --from-beginning
 */
public class B_StatefulLeftJoinOperation {

    private static final Logger logger = LoggerFactory.getLogger(B_StatefulLeftJoinOperation.class);

    public static void main(String[] args) throws InterruptedException {
        Properties props = buildStreamsProperties();
        Topology topology = buildTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);

        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
            logger.info("The WordProcessorApp is gracefully shutting down");
        }));

        streams.start();
        logger.info("WordProcessorApp is started");

        latch.await();
    }

    private static Properties buildStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "left-join-app");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);

        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> userInfoSrc = builder.stream(
                "user-info",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("source-user-info")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        KStream<String, String> userAddressSrc = builder.stream(
                "user-address",
                Consumed.with(Serdes.String(), Serdes.String()).
                        withName("source-user-address")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        KStream<String, String> userInfoKeyed = userInfoSrc.map((k, v) -> KeyValue.pair(v.split(",")[0], v),
                Named.as("user-info-selectKey"));

        KStream<String, String> userAddressKeyed = userAddressSrc.map((k, v) -> KeyValue.pair(v.split(",")[0], v),
                Named.as("user-address-selectKey"));

        userInfoKeyed.leftJoin(userAddressKeyed, (left, right) -> left + "---" + right, JoinWindows.of(Duration.ofMinutes(1)),
                StreamJoined.with(Serdes.String(), Serdes.String(), Serdes.String()))
                .print(Printed.<String, String>toSysOut().withLabel("left-join"));


        return builder.build();
    }
}
