package com.jskno.launcher.b_join;

import com.jskno.topology.b_join.F_ExploreKStreamOuterJoinKStreamTopology;
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
import org.apache.kafka.streams.Topology;

@Slf4j
public class F_JoiningKStreamKStreamLauncher {

    private static AdminClient adminClient;

    public static void main(String[] args) {

        Properties streamProperties = buildStreamProperties();
        createTopics(streamProperties,
            List.of(
                F_ExploreKStreamOuterJoinKStreamTopology.ALPHABETS,
                F_ExploreKStreamOuterJoinKStreamTopology.ALPHABETS_ABBREVIATIONS));

        Topology kTableTopology = F_ExploreKStreamOuterJoinKStreamTopology.build();
        KafkaStreams kafkaStreams = new KafkaStreams(kTableTopology, streamProperties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

        log.info("Starting Alphabets stream");
        kafkaStreams.start();
    }

    private static Properties buildStreamProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "joins1");
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "5000");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return properties;
    }

    private static void createTopics(Properties streamProperties, List<String> alphabets) {
        if (adminClient == null) {
            adminClient = AdminClient.create(streamProperties);
        }

        List<String> notExistingTopics = getNotExistingTopics(alphabets);

        if (!notExistingTopics.isEmpty()) {
            var partitions = 1;
            short replication = 1;

            var newTopics = notExistingTopics.stream()
                .map(topic -> new NewTopic(topic, partitions, replication))
                .toList();

            try {
                var createTopicResult = adminClient.createTopics(newTopics);
                createTopicResult.all().get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception creating topics : {} ", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }


    private static List<String> getNotExistingTopics(List<String> alphabets) {
        try {
            Collection<TopicListing> existingTopics = adminClient.listTopics().listings().get();
            List<String> existingTopicsNames = existingTopics.stream().map(TopicListing::name).toList();
            return alphabets.stream()
                .filter(s -> !existingTopicsNames.contains(s))
                .toList();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

}
