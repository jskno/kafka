package com.jskno.launcher.b_join;

import com.jskno.topology.b_join.A_ExploreKStreamJoinKTableTopology;
import com.jskno.topology.b_join.C_ExploreKTableJoinKTableTopology;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

@Slf4j
public class C_JoiningKTabkeKTableStreamLauncher {

    public static void main(String[] args) {

        Properties streamProperties = buildStreamProperties();
        createTopics(streamProperties,
            List.of(A_ExploreKStreamJoinKTableTopology.ALPHABETS, A_ExploreKStreamJoinKTableTopology.ALPHABETS_ABBREVIATIONS));

        Topology kTableTopology = C_ExploreKTableJoinKTableTopology.build();
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
        AdminClient adminClient = AdminClient.create(streamProperties);

        var partitions = 1;
        short replication = 1;

        var newTopics = alphabets.stream()
            .map(topic -> new NewTopic(topic, partitions, replication))
            .toList();

        var createTopicResult = adminClient.createTopics(newTopics);
        try {
            createTopicResult.all().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception creating topics : {} ",e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
