package com.jskno.h_ktable;

import com.jskno.h_ktable.model.Employee;
import com.jskno.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic employee --partitions 3
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --delete --topic employee

// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic employee --property parse.key=true --property key.separator=:
// ./bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic employee-cloud-less-65-with-title --property print.key=true --property print.value=true --property key.separator=:
public class C_KTableBasicOperationsApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(C_KTableBasicOperationsApp.class);

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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktabke-basic-operations");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        // Employee event example:
        KTable<String, Employee> kTable = builder.table(
                        "employee",
                        Consumed.with(Serdes.String(), JsonSerdes.of(Employee.class)).
                                withName("source-processor")
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST));

        kTable
                .filter((k, v) -> {
                    LOGGER.info("Filter key: " + k + " value: " + v);
                    return "Data&Cloud".equalsIgnoreCase(v.getDepartment());
                })
                .filterNot((k, v) -> {
                    LOGGER.info("FilterNot key: " + v + " value: " + v);
                    return v.getAge() > 65;
                })
                .mapValues(employee -> {
                    LOGGER.info("Mapping value: " + employee.getAge());
                    return Employee.newBuilder(employee).evaluateTitle().build();
                })
                .toStream()
                .to("employee-cloud-less-65-with-title", Produced.with(Serdes.String(), JsonSerdes.of(Employee.class)));

        return builder.build();
    }
}
