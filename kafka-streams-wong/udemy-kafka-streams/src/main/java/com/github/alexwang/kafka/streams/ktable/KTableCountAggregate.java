package com.github.alexwang.kafka.streams.ktable;

import com.github.alexwang.kafka.streams.model.Employee;
import com.github.alexwang.kafka.streams.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class KTableCountAggregate {
    private final static Logger LOG = LoggerFactory.getLogger(KTableBasisOperationApp.class);
    private final static String APP_ID = "ktable_count_aggregate_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "employee";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, Employee> kTable = builder.table(SOURCE_TOPIC, Consumed.with(Serdes.String(), JsonSerdes.EmployeeSerde()).withName("source")
                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        kTable.groupBy((k, v) -> KeyValue.pair(v.getDepartment(), v), Grouped.with(Serdes.String(), JsonSerdes.EmployeeSerde()))
                .count(Named.as("count"), Materialized.with(Serdes.String(), Serdes.Long()))
                .toStream()
                .print(Printed.<String, Long>toSysOut().withLabel("count-employee-per-department"));

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
