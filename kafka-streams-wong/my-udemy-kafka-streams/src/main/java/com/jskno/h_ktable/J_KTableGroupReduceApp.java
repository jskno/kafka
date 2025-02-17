package com.jskno.h_ktable;

import com.jskno.h_ktable.model.Employee;
import com.jskno.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
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
public class J_KTableGroupReduceApp {


    private final static Logger LOGGER = LoggerFactory.getLogger(J_KTableGroupReduceApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktable-group-reduce-operations");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KTable<String, Employee> employeeKTable = builder.table(
                "employee",
                Consumed.with(Serdes.String(), JsonSerdes.of(Employee.class))
                        .withName("employee-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));

        employeeKTable
                .toStream()
                .peek((k, v) -> LOGGER.info("Before GroupBy - Key: [{}], Value: [{}]", k, v))
                .filter((k, v) -> v != null && v.getDepartment() != null)
                .toTable()
                .groupBy((k, v) -> KeyValue.pair(v.getDepartment(), v),
                        Grouped.with(Serdes.String(), JsonSerdes.of(Employee.class)))
                .reduce(
                        // Adder
                        (currentAgg, newValue) -> {
                            LOGGER.info("adder--currentAgg:{},newValue:{}", currentAgg, newValue);
                            Employee employee = Employee.newBuilder(currentAgg).build();
                            employee.setTotalSalary(currentAgg.getTotalSalary() + newValue.getSalary());
                            return employee;
                        },
                        //subtractor
                        (currentAgg, oldValue) -> {
                            LOGGER.info("subtractor--currentAgg:{},oldValue:{}", currentAgg, oldValue);
                            Employee employee = Employee.newBuilder(currentAgg).build();
                            employee.setTotalSalary(currentAgg.getTotalSalary() - oldValue.getSalary());
                            return employee;
                        },
                        Named.as("reducer"),
                        Materialized.as("Total-salary-per-department"))
                .toStream()
                .foreach((k, v) -> LOGGER.info("department:[{}],total salary:[{} USD]", k, v.getTotalSalary()));


        return builder.build();
    }

}
