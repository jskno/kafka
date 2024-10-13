package com.github.alexwang.kafka.streams.windowing;

import com.github.alexwang.kafka.streams.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * The tumbling window operation for net traffic attack analysis by real-time approach
 *
 * @see com.github.alexwang.kafka.streams.model.NetTraffic
 */
public class TumblingTimeWindowWithSuppressionApp {
    private final static Logger LOG = LoggerFactory.getLogger(TumblingTimeWindowWithSuppressionApp.class);
    private final static String APP_ID = "suppression_window_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "net.traffic.logs";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        conf.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        conf.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());

        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), JsonSerdes.NetTrafficSerde())
                        .withName("source-processor").withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .groupBy((k, v) -> v.getRemoteAddress(), Grouped.with(Serdes.String(), JsonSerdes.NetTrafficSerde()))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
                .count(Named.as("tumbling-count"), Materialized.as("tumbling-count-state-store"))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .filter((k, v) -> v >= 10)
                .toStream()
                .print(Printed.<Windowed<String>, Long>toSysOut().withLabel("attack"));

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
