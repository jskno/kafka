package com.github.alexwang.kafka.streams.stateful;

import com.github.alexwang.kafka.streams.model.SalesStats;
import com.github.alexwang.kafka.streams.serdes.JsonSerdes;
import com.github.alexwang.kafka.streams.statestore.QueryableStateStoreServer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StatefulAggregateSalesStatsOperation {
    private final static Logger LOG = LoggerFactory.getLogger(StatefulAggregateSalesStatsOperation.class);
    private final static String APP_ID = "stateful_aggregate_sales_stats_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "sales";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, args[0]);
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
        conf.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), JsonSerdes.SalesSerde())
                        .withName("source-processor").withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                //.selectKey((k,v)->v.getDepartment())
                .groupBy((k, v) -> v.getDepartment(), Grouped.with(Serdes.String(), JsonSerdes.SalesSerde()))
                .aggregate(SalesStats::new, (dept, sales, salesStatsAggr) -> {
                            if (salesStatsAggr.getDepartment() == null) {
                                salesStatsAggr.setDepartment(sales.getDepartment());
                                salesStatsAggr.setCount(1);
                                salesStatsAggr.setTotalAmount(sales.getSalesAmount());
                                salesStatsAggr.setAverageAmount(sales.getSalesAmount());
                            } else {
                                salesStatsAggr.setCount(salesStatsAggr.getCount() + 1);
                                salesStatsAggr.setTotalAmount(salesStatsAggr.getTotalAmount() + sales.getSalesAmount());
                                salesStatsAggr.setAverageAmount(salesStatsAggr.getTotalAmount() / salesStatsAggr.getCount());
                            }
                            return salesStatsAggr;
                        }, Named.as("aggregate-processor"),
                        Materialized.<String, SalesStats, KeyValueStore<Bytes, byte[]>>as("sales-stats").withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.SalesStatsSerde()))
                .toStream()
                .print(Printed.<String, SalesStats>toSysOut().withLabel("sales-stats"));

        Topology topology = builder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, conf);

        new QueryableStateStoreServer(kafkaStreams, "sales-stats").start();
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
