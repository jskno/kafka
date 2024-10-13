package com.github.alexwang.kafka.streams.stateful;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class StatefulCountAggregationOperation {
    private final static Logger LOG = LoggerFactory.getLogger(StatefulCountAggregationOperation.class);
    private final static String APP_ID = "stateful_count_aggregation_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "input.words";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        //1.cache.max.bytes.buffering
        //2.commit.interval.ms
        conf.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        StreamsBuilder builder = new StreamsBuilder();
        //<null, this is kafka streams>
        //<null, I like kafka streams>
        builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), Serdes.String()).withName("source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                //<this,1L>
                //<is,1L>
                .flatMap((k, v) -> Arrays.stream(v.split("\\s+")).map(e -> KeyValue.pair(e, 1L)).collect(Collectors.toList()), Named.as("flatmap-words"))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .count(Named.as("word-count"), Materialized.as("word-count"))
                .toStream()
                .print(Printed.<String, Long>toSysOut().withLabel("wc"));


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
