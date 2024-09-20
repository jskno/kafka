package com.jskno.launcher.a_aggregate;

import com.jskno.topology.a_aggregate.D_ExploreAggregateOperatorTopologyV4;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

@Slf4j
public class D_AggregatingStreamLauncherV4 {

    public static void main(String[] args) {
        Properties properties = buildProperties();
        createTopics(properties, List.of(D_ExploreAggregateOperatorTopologyV4.AGGREGATE));

        Topology kTableTopoligy = D_ExploreAggregateOperatorTopologyV4.build();
        KafkaStreams kafkaStreams = new KafkaStreams(kTableTopoligy, properties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

        log.info("Starting Aggregate Streams");
        kafkaStreams.start();
    }

    private static Properties buildProperties() {
        Properties properties = new Properties();

        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "aggregate-app");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return properties;
    }

    private static void createTopics(Properties properties, List<String> topics) {
        AdminClient admin = AdminClient.create(properties);
        var partitions = 1;
        short replication  = 1;

        var newTopics = topics
            .stream()
            .map(topic ->{
                return new NewTopic(topic, partitions, replication);
            })
            .collect(Collectors.toList());

        var createTopicResult = admin.createTopics(newTopics);
        try {
            createTopicResult.all().get();
            log.info("topics are created successfully");
        } catch (Exception e) {
            log.error("Exception creating topics : {} ",e.getMessage(), e);
        }
    }

}
