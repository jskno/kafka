package com.jskno.launcher.ktable;

import com.jskno.topology.ktable.ExploreKTableTopology;
import com.jskno.topology.ktable.ExploreKTableTopologyV3;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;

@Slf4j
public class KTableStreamAppV3 {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "ktable");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 30000);

        createTopics(properties, Set.of(ExploreKTableTopology.WORDS));

        var kTableTopology = ExploreKTableTopologyV3.build();

        var kafkaStreams = new KafkaStreams(kTableTopology, properties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        log.info("Starting Words streams");
        kafkaStreams.start();

    }

    private static void createTopics(Properties properties, Set<String> topics) {
        try (AdminClient adminClient = AdminClient.create(properties)) {

            Set<String> existingTopicNames = adminClient.listTopics().names().get();
            List<String> union = existingTopicNames.stream()
                .filter(s -> s.contains("words"))
                .toList();
            adminClient.deleteTopics(union).all().get();

            var partitions = 2;
            short replication = 1;

            var newTopics = topics.stream()
                .map(topic -> new NewTopic(topic, partitions, replication))
                .toList();

            var createTopicsResult = adminClient.createTopics(newTopics);
            createTopicsResult.all().get();
            log.info("Topics are created successfully");
        } catch (Exception ex) {
            log.error("Exception creating topics: {}", ex.getMessage(), ex);
        }
    }



}
