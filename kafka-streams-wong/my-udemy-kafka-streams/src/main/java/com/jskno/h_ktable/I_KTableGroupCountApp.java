package com.jskno.h_ktable;

import com.jskno.h_ktable.model.Employee;
import com.jskno.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic employee
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic employee --partitions 3

// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic employee property parse.key=true --property key.separator=:

// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic employee \
// --property key.separator=: --property print.key=true --property print.value=true --from-beginning

/*
./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic ktable-group-counting-operations-KTABLE-AGGREGATE-STATE-STORE-0000000004-repartition \
--property key.separator=: --property print.key=true --property print.value=true --from-beginning

./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
--topic ktable-group-counting-operations-KTABLE-AGGREGATE-STATE-STORE-0000000004-repartition \
--property key.separator=: --property print.key=true --property print.value=true --partition 1 --offset 0
 */
public class I_KTableGroupCountApp {


    private final static Logger LOGGER = LoggerFactory.getLogger(I_KTableGroupCountApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktable-group-counting-operations");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore4");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, Employee> employeeKTable = builder.table(
                "employee",
                Consumed.with(Serdes.String(), JsonSerdes.of(Employee.class))
                        .withName("employee-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        employeeKTable
                .groupBy((k, v) -> KeyValue.pair(v.getDepartment(), v),
                        Grouped.with(Serdes.String(), JsonSerdes.of(Employee.class)))
                .count(Named.as("count"), Materialized.with(Serdes.String(), Serdes.Long()))
                .toStream()
                .print(Printed.<String, Long>toSysOut().withLabel("employees-by-department"));

        return builder.build();
    }

}
