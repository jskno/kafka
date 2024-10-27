package com.linuxacademy.ccdak.my_stateful.aggregations;

import com.linuxacademy.ccdak.constants.AppConstants;
import com.linuxacademy.ccdak.excetion.AppException;
import com.linuxacademy.ccdak.model.Customer;
import com.linuxacademy.ccdak.serdes.SerdesFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class c_ReduceMain {

    public static final Logger log = LoggerFactory.getLogger(c_ReduceMain.class);

    public static void main(String[] args) {
        Properties properties = buildStreamsProperties();
        Topology topology = buildTopology();
        log.info("Printing topology...");
        log.info(topology.describe().toString());

        try (KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {
            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaStreams.close();
                latch.countDown();
                log.info("Closing gracefully !!");
            }));

            log.info("Starting streams...");
            kafkaStreams.start();
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties buildStreamsProperties() {
        Properties properties = readConfig();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "customer-reduce");

        properties.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 10485760);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    private static Properties readConfig() {
        if(!Files.exists(Paths.get(AppConstants.CONFIG_FILE))) {
            throw new AppException("File not found");
        }

        Properties properties = new Properties();
        try(InputStream is = new FileInputStream(AppConstants.CONFIG_FILE)) {
            properties.load(is);
        } catch (FileNotFoundException e) {
            throw new AppException("File not there");
        } catch (IOException e) {
            throw new AppException("File not ready");
        }

        return properties;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Customer> source = builder.stream(AppConstants.FLATMAP_TOPIC,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Customer.class)));

        KStream<String, Customer> reKeyByGender = source.selectKey((s, customer) -> customer.getGender());

        KTable<String, Long> totalYearsActiveByGender = reKeyByGender
            .map((s, customer) -> KeyValue.pair(s, (long) customer.getYearsActive()))
            .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
            .reduce(Long::sum, Materialized.with(Serdes.String(), Serdes.Long()));

        totalYearsActiveByGender.toStream().peek((s, integer) -> log.info("Total active years are: " + integer + " for " + s));

        totalYearsActiveByGender.toStream().to(AppConstants.ACTIVE_YEARS_BY_GENDER_REDUCE, Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }

}
