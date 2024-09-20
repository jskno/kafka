package com.jskno.launcher.c_window;

import com.jskno.topology.c_window.A_ExploreCountTumblingWindowTopology;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;

@Slf4j
public class A_WindowStreamLauncher {

    private static AdminClient adminClient;

    public static void main(String[] args) {
        Properties properties = buildStreamProperties();
        createTopics(properties, List.of(A_ExploreCountTumblingWindowTopology.WINDOW_WORDS));

        var topology = A_ExploreCountTumblingWindowTopology.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

        log.info("Starting Windows streams");
        kafkaStreams.start();

    }

    private static Properties buildStreamProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "windows-1");
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "10000");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return properties;
    }

    private static void createTopics(Properties properties, List<String> topics) {
        adminClient = AdminClient.create(properties);

        List<String> existingTopics = getExistingTopics();

        List<String> topicsToBeCreated = topics.stream()
            .filter(s -> !existingTopics.contains(s))
            .toList();

        if (!topicsToBeCreated.isEmpty()) {
            createNewTopics(topicsToBeCreated);
        }
    }

    private static List<String> getExistingTopics() {
        try {
            Collection<TopicListing> topicListings = adminClient.listTopics().listings().get();
            return topicListings.stream()
                .map(TopicListing::name)
                .toList();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private static void createNewTopics(List<String> topicsToBeCreated) {

        var partitions = 2;
        short replication = 1;

        List<NewTopic> list = topicsToBeCreated.stream()
            .map(topic -> new NewTopic(topic, partitions, replication))
            .toList();

        var createTopicsResult = adminClient.createTopics(list);
        try {
            createTopicsResult.all().get();
            log.info("topics are created successfully");
        } catch (Exception e) {
            log.error("Exception creating topics : {} ",e.getMessage(), e);
        }
    }

}
