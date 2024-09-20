package com.jskno;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

/**
 * Hello world!
 *
 */
public class FavouriteColorApp {

    private static final List<String> targetColors = Arrays.asList("green", "red", "blue");

    public static void main( String[] args ) {

        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-colour-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        config.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");

        KafkaStreams streams = new KafkaStreams(createTopology(), config);

        streams.start();

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        // print the topology every 10 seconds for learning purposes
        while (true) {
            streams.localThreadsMetadata().forEach(data -> System.out.println(data));
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            }
        }

    }

    private static Topology createTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> textLines = builder.stream("favourite-colour-input");

        KStream<String, String> usersAndColors = textLines
            .filter((k, v) -> v.contains(","))
            .selectKey((k,v) -> v.split(",")[0].toLowerCase())
            .mapValues(v -> v.split(",")[1].toLowerCase())
            .filter((user, colour) -> targetColors.contains(colour));

        usersAndColors.to("user-keys-and-colours");

        KTable<String, String> usersAndColoursTable = builder.table("user-keys-and-colours");

        KTable<String, Long> favouriteColours = usersAndColoursTable
            .groupBy((user, colour) -> new KeyValue<>(colour, colour))
            .count(Materialized.as("CountsByColours"));

        favouriteColours.toStream().to("favourite-colour-output", Produced.with(Serdes.String(), Serdes.Long()));

        return builder.build();
    }

}
