package com.jskno.e_statestore;

import com.jskno.d_stateful_aggregation.model.Sales;
import com.jskno.d_stateful_aggregation.model.SalesStats;
import com.jskno.serdes.JsonSerdes;
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


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic word-processor-input
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic word-processor-output --from-beginning
public class A1_Stateful_QueryableApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(A1_Stateful_QueryableApp.class);

    public static void main(String[] args) throws InterruptedException {
        Properties props = buildStreamsProperties();
        Topology topology = buildTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);

        new A0_QueryableStateStoreServer(streams, "sales-stats-query").start();

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "query-sales-app");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);

        // These two props control how often the stream task is entirely consume and process until final operation its topology
        //props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);  // deprecated
        props.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        //props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // <null, This is Kafka Stream>
        // <null, I like Kafka Stream>
        KStream<String, Sales> sourceStream = builder.stream(
                "sales",
                Consumed.with(Serdes.String(), JsonSerdes.of(Sales.class)).
                        withName("sales-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        sourceStream
                .groupBy((k, v) -> v.getDepartment(), Grouped.with(Serdes.String(), JsonSerdes.of(Sales.class)))
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
                }, Named.as("aggregate-query-processor"),
                        Materialized.<String, SalesStats, KeyValueStore<Bytes, byte[]>>as("sales-stats-query")
                                .withKeySerde(Serdes.String())
                                .withValueSerde(JsonSerdes.of(SalesStats.class)))
                .toStream()
                .print(Printed.<String, SalesStats>toSysOut().withLabel("sales-stats-query"));

        return builder.build();
    }

    private static Sales populateTotalAmount(Sales sales) {
        if (sales.getSalesAmount() != sales.getTotalSalesAmount()) {
            sales.setTotalSalesAmount(sales.getSalesAmount());
        }
        return sales;
    }
}
