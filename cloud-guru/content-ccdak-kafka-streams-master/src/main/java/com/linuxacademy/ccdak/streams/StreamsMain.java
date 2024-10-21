package com.linuxacademy.ccdak.streams;

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
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamsMain {

    private final static Logger LOG = LoggerFactory.getLogger(StreamsMain.class);

    public static void main(String[] args) throws InterruptedException {
        Properties properties = buildStreamsProperties();
        Topology topology = buildStreamTopology();
        try(final KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {

            final CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaStreams.close();
                latch.countDown();
                LOG.info("The kafka streams application is graceful closed.");
            }));

            kafkaStreams.start();
            LOG.info("The kafka streams application start...");
            latch.await();
        }
    }

    private static Properties buildStreamsProperties() {
        Properties properties = readConfig();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-invetory");
        //properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        //properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    private static Properties readConfig() {
        if (!Files.exists(Paths.get(AppConstants.CONFIG_FILE))) {
            throw new AppException(AppConstants.CONFIG_FILE + " not found.");
        }

        final Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(AppConstants.CONFIG_FILE)) {
            properties.load(inputStream);
        } catch (FileNotFoundException e) {
            throw new AppException(AppConstants.CONFIG_FILE + " not found.");
        } catch (IOException e) {
            throw new AppException(AppConstants.CONFIG_FILE + " not ready to be opened??.");
        }

        return properties;
    }


    private static Topology buildStreamTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Inventory> source = builder.stream(
            AppConstants.INVENTORY_TOPIC,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Inventory.class)));

        source.to(
            AppConstants.INVENTORY_OUTPUT_STREAM,
            Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(Inventory.class)));

        return builder.build();
    }

}
