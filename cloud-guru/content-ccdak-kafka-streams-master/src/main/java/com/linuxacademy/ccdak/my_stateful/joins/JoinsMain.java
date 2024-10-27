package com.linuxacademy.ccdak.my_stateful.joins;

import com.linuxacademy.ccdak.constants.AppConstants;
import com.linuxacademy.ccdak.excetion.AppException;
import com.linuxacademy.ccdak.model.VehicleEnhance;
import com.linuxacademy.ccdak.model.VehicleIndicators;
import com.linuxacademy.ccdak.model.VehicleLocation;
import com.linuxacademy.ccdak.serdes.SerdesFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class JoinsMain {

    public static void main(String[] args) {
        Properties properties = buildStreamsProperties();
        Topology topology = buildTopology();

        try(KafkaStreams kafkaStreams = new KafkaStreams(topology, properties)) {
            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                kafkaStreams.close();
                latch.countDown();
            }));

            kafkaStreams.start();
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties buildStreamsProperties() {
        Properties properties = readConfig();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-join-streams-app");

        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    private static Properties readConfig() {
        if(!Files.exists(Paths.get(AppConstants.CONFIG_FILE))) {
            throw new AppException("File does not exist");
        }

        Properties properties = new Properties();
        try(InputStream is = new FileInputStream(AppConstants.CONFIG_FILE)) {
            properties.load(is);
        } catch (FileNotFoundException e) {
            throw new AppException("File not found", e);
        } catch (IOException e) {
            throw new AppException("File not ready", e);
        }

        return properties;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, VehicleLocation> locationsStream = builder.stream(
            AppConstants.TOPIC_VEHICLE_LOCATION,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(VehicleLocation.class)));

        KStream<String, VehicleIndicators> indicatorsStream = builder.stream(AppConstants.TOPIC_VEHICLE_INDICATORS,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(VehicleIndicators.class)));

        ValueJoiner<VehicleLocation, VehicleIndicators, VehicleEnhance> valueJoiner = VehicleEnhance::buildVehicleEnhance;
        JoinWindows joinWindows = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMinutes(5));
        StreamJoined<String, VehicleLocation, VehicleIndicators> joinParams = StreamJoined.with(
                Serdes.String(), SerdesFactory.jsonSerdes(VehicleLocation.class), SerdesFactory.jsonSerdes(VehicleIndicators.class));

        locationsStream
            .join(indicatorsStream, valueJoiner, joinWindows, joinParams)
            .to(AppConstants.INNER_JOIN_TOPIC, Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(VehicleEnhance.class)));

        locationsStream
            .leftJoin(indicatorsStream, valueJoiner, joinWindows, joinParams)
            .to(AppConstants.LEFT_JOIN_TOPIC, Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(VehicleEnhance.class)));

        locationsStream
            .outerJoin(indicatorsStream, valueJoiner, joinWindows, joinParams)
            .to(AppConstants.OUTER_JOIN_TOPIC, Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(VehicleEnhance.class)));

        return builder.build();
    }

}
