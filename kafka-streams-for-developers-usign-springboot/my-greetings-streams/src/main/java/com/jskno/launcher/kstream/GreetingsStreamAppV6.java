package com.jskno.launcher.kstream;

import com.jskno.serdes.harcoded.GreetingSerdes;
import com.jskno.topology.kstream.GreetingsTopology;
import com.jskno.topology.kstream.GreetingsTopologyV5;
import java.util.Properties;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;

@Slf4j
public class GreetingsStreamAppV6 {

    private static final Set<String> TOPICS = Set.of(
        GreetingsTopology.GREETINGS,
        GreetingsTopology.GREETINGS_UPPERCASE,
        GreetingsTopology.GREETINGS_SPANISH);

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "greetings-app");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, GreetingSerdes.class);

        properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, "2");
        properties.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG,
            LogAndContinueExceptionHandler.class);

        createTopics(properties, TOPICS);
        var greetingsTopology = GreetingsTopologyV5.buildTopology();

        var kafkaStreams = new KafkaStreams(greetingsTopology, properties);

        // This works if you setup in settings Gradle Run with IntellijIdea
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

//        try (var kafkaStreams = new KafkaStreams(greetingsTopology, properties)) {
        try {
            kafkaStreams.start();
        } catch (Exception ex) {
            log.error("Exception in starting the stream: {}", ex.getMessage(), ex);
        }
    }

    private static void createTopics(Properties config, Set<String> greetings) {

        try (AdminClient adminClient = AdminClient.create(config)) {

            Set<String> existingTopicNames = adminClient.listTopics().names().get();
//            adminClient.deleteTopics(existingTopicNames).all().get();

            var partitions = 2;
            short replication = 1;

            var newTopics = greetings.stream()
                .filter(newTopicName -> !existingTopicNames.contains(newTopicName))
                .map(topic -> new NewTopic(topic, partitions, replication))
                .toList();

            var createTopicsResult = adminClient.createTopics(newTopics);
            createTopicsResult
                .all().get();
            log.info("Topics are created successfully");
        } catch (Exception ex) {
            log.error("Exception creating topics: {}", ex.getMessage(), ex);
        }
    }

}
