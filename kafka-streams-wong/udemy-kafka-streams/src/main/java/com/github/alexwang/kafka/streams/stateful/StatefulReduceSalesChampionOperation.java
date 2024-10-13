package com.github.alexwang.kafka.streams.stateful;

import com.github.alexwang.kafka.streams.model.Sales;
import com.github.alexwang.kafka.streams.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StatefulReduceSalesChampionOperation {
    private final static Logger LOG = LoggerFactory.getLogger(StatefulReduceSalesChampionOperation.class);
    private final static String APP_ID = "stateful_reduce_sales_champion_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "sales";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        conf.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);

        //accumulate
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(SOURCE_TOPIC, Consumed.with(Serdes.String(), JsonSerdes.SalesSerde())
                        .withName("sales-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .mapValues(StatefulReduceSalesChampionOperation::populateTotalAmount, Named.as("populate-transform"))
                .groupBy((k, v) -> v.getUserName(), Grouped.with(Serdes.String(), JsonSerdes.SalesSerde()))
                .reduce((aggValue, currentValue) -> Sales.newBuild(currentValue).accumulateSalesAmount(aggValue.getTotalSalesAmount()).build(),
                        Named.as("accumulate-sales-amount"), Materialized.as("accumulate-sales"))
                .toStream()
                .groupBy((k, v) -> v.getDepartment(), Grouped.with(Serdes.String(), JsonSerdes.SalesSerde()))
                .reduce((aggValue, currentValue) -> currentValue.getTotalSalesAmount() > aggValue.getTotalSalesAmount() ? currentValue : aggValue
                        , Named.as("sales-champion-reducer"), Materialized.as("sales-champion"))
                .toStream()
                .print(Printed.<String, Sales>toSysOut().withLabel("sales-champion"));

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

    private static Sales populateTotalAmount(Sales sales) {
        if (sales.getSalesAmount() != sales.getTotalSalesAmount()) {
            sales.setTotalSalesAmount(sales.getSalesAmount());
        }
        return sales;
    }
}
