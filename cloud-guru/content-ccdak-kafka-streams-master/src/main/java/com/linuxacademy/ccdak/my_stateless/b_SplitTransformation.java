package com.linuxacademy.ccdak.my_stateless;

import com.linuxacademy.ccdak.constants.AppConstants;
import com.linuxacademy.ccdak.excetion.AppException;
import com.linuxacademy.ccdak.model.Inventory;
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
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class b_SplitTransformation {

    private final static Logger LOG = LoggerFactory.getLogger(b_SplitTransformation.class);

    public static void main(String[] args) {
        Properties properties = buildStreamsProperties();
        Topology topology = buildTopology();
        LOG.info(topology.describe().toString());

        try(KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {
            final CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaStreams.close();
                latch.countDown();
                LOG.info("The kafka streams application is graceful closed.");
            }));

            kafkaStreams.start();
            latch.await();

        } catch (final Throwable e) {
            LOG.error(e.getMessage());
            System.exit(1);
        }
        System.exit(0);
    }

    private static Properties buildStreamsProperties() {
        Properties properties = readConfig();
        // Mandatory
        //properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "InTheReadConfigFile");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "split-transformation-inventory");
        // End Mandatory

        //properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        //properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.STATESTORE_CACHE_MAX_BYTES_CONFIG, 0);
        return properties;
    }

    private static Properties readConfig() {
        if (!Files.exists(Paths.get(AppConstants.CONFIG_FILE))) {
            throw new AppException("File not found: " + AppConstants.CONFIG_FILE);
        }

        Properties properties = new Properties();
        try(InputStream is = new FileInputStream(AppConstants.CONFIG_FILE)) {
            properties.load(is);
        } catch (FileNotFoundException e) {
            throw new AppException("Something wrong, the file not found" + AppConstants.CONFIG_FILE, e);
        } catch (IOException e) {
            throw new AppException("Something wrong, the file is not ready to be processed" + AppConstants.CONFIG_FILE, e);
        }

        return properties;
    }


    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<Long, Inventory> source = builder.stream(AppConstants.INVENTORY_TOPIC,
            Consumed.with(Serdes.Long(), SerdesFactory.jsonSerdes(Inventory.class)));

        source.split(Named.as("branch-"))
            .branch((key, value) -> key < 50L, Branched.withConsumer(ks -> ks.to(AppConstants.INVENTORY_OUTPUT_LESS_50)))
            .branch((key, value) -> key >= 50L, Branched.withConsumer(ks -> ks.to(AppConstants.INVENTORY_OUTPUT_GREATER_50)));

        return builder.build();
    }

}
