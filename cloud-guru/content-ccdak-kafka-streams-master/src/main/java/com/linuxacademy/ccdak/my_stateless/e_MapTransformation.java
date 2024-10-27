package com.linuxacademy.ccdak.my_stateless;

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
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class e_MapTransformation {

    private static final Logger log = LoggerFactory.getLogger(e_MapTransformation.class);

    public static void main(String[] args) {
        Properties properties = buildStreamsProperties();
        Topology topology = buildTopology();
        log.info("Describing Topology:");
        log.info(topology.describe().toString());

        try(KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {

            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Closing streams gracefully...");
                kafkaStreams.close();
                latch.countDown();
            }));

            kafkaStreams.start();
            log.info("The kafka streams application start...");
            latch.await();
        } catch (InterruptedException e) {
            throw new AppException("Whatever", e);
        }
    }

    private static Properties buildStreamsProperties() {
        Properties properties = readConfig();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "inventory-flatmap-output");

        properties.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
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

        KStream<String, Customer> stream = builder.stream(AppConstants.FLATMAP_TOPIC,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Customer.class)));

        KStream<String, Customer> map = stream
            .map((key, customer) -> KeyValue.pair(key, new Customer(customer, customer.getIncome() * 2)));

        map.peek((key, customer) -> log.info("key=" + key + ", value=" + customer));

        map.to(AppConstants.MAP_OUTPUT, Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(Customer.class)));

        return builder.build();
    }

}
