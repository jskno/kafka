package com.github.alexwang.kafka.streams.ktable;

import com.github.alexwang.kafka.streams.model.Employee;
import com.github.alexwang.kafka.streams.model.EmployeeStats;
import com.github.alexwang.kafka.streams.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class KTableAggregateAggregate {
    private final static Logger LOG = LoggerFactory.getLogger(KTableAggregateAggregate.class);
    private final static String APP_ID = "ktable_aggregate_aggregate_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String SOURCE_TOPIC = "employee";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, Employee> table = builder.table(SOURCE_TOPIC, Consumed.with(Serdes.String(), JsonSerdes.EmployeeSerde()).withName("source")
                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        table.groupBy((k, v) -> KeyValue.pair(v.getDepartment(), v), Grouped.with(Serdes.String(), JsonSerdes.EmployeeSerde()))
                .aggregate(EmployeeStats::new, (key, newValue, aggregate) -> {
                            LOG.info("adder--{},{},{}", key, newValue, aggregate);
                            if (aggregate.getDepartment() == null) {
                                aggregate.setDepartment(newValue.getDepartment());
                                aggregate.setTotalSalary(newValue.getSalary());
                            } else {
                                aggregate.setTotalSalary(aggregate.getTotalSalary() + newValue.getSalary());
                            }
                            return aggregate;
                        }, (key, oldValue, aggregate) -> {
                            LOG.info("sub--{},{},{}", key, oldValue, aggregate);
                            aggregate.setTotalSalary(aggregate.getTotalSalary() - oldValue.getSalary());
                            return aggregate;
                        },
                        Named.as("aggregate"),
                        Materialized.<String, EmployeeStats, KeyValueStore<Bytes, byte[]>>as("aggregate").withValueSerde(JsonSerdes.EmployeeStatsSerde()))
                .toStream()
                .print(Printed.<String, EmployeeStats>toSysOut().withLabel("total-salary-per-department"));

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
