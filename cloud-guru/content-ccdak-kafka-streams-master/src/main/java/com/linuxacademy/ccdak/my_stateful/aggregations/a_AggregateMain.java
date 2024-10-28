package com.linuxacademy.ccdak.my_stateful.aggregations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.linuxacademy.ccdak.constants.AppConstants;
import com.linuxacademy.ccdak.excetion.AppException;
import com.linuxacademy.ccdak.model.Inventory;
import com.linuxacademy.ccdak.model.InventoryEvents;
import com.linuxacademy.ccdak.serdes.SerdesFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class a_AggregateMain {

    private static final Logger log = LoggerFactory.getLogger(a_AggregateMain.class);

    public static void main(String[] args) {
        Properties properties = buildStreamsProperties();
        Topology topology = buildTopology();
        log.info("Printing topology:");
        log.info(topology.describe().toString());

        try(KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {
            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Closing stream gracefully");
                kafkaStreams.close();
                latch.countDown();
            }));

            kafkaStreams.start();
            log.info("Stream started...");
            latch.await();
        } catch (InterruptedException e) {
            throw new AppException("Whatever", e);
        }
    }

    private static Properties buildStreamsProperties() {
        Properties properties = readConfig();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "inventory-aggregate");

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

        KStream<Long, Inventory> source = builder.stream(AppConstants.INVENTORY_TOPIC,
            Consumed.with(Serdes.Long(), SerdesFactory.jsonSerdes(Inventory.class)));

        KGroupedStream<Long, Inventory> groupedStream = source.groupByKey();

        Initializer<InventoryEvents> inventoryEventsInitializer = InventoryEvents::new;

        Aggregator<Long, Inventory, InventoryEvents> aggregator = (key, inventory, inventoryEvents) -> inventoryEvents.add(inventory);

        KTable<Long, InventoryEvents> inventoryEventsTable = groupedStream.aggregate(
            inventoryEventsInitializer,
            aggregator,
            Materialized.<Long, InventoryEvents, KeyValueStore<Bytes, byte[]>>as("Whatever")
                .withKeySerde(Serdes.Long())
                .withValueSerde(SerdesFactory.jsonSerdes(InventoryEvents.class)));

        inventoryEventsTable.toStream().to(AppConstants.INVENTORY_AGGREGATE,
            Produced.with(Serdes.Long(), SerdesFactory.jsonSerdes(InventoryEvents.class)));

        return builder.build();
    }

}