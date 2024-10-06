package com.jskno.launcher.e_my_exercises;

import com.jskno.constants.OrdersConstants;
import com.jskno.topology.e_my_exercises.A_KStreamJoinKTableTopology;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

@Slf4j
public class A_KStreamJoinKTableLauncher {

    private static final Set<String> topics = Set.of(
        OrdersConstants.ORDERS_TOPIC, OrdersConstants.STORES_TOPIC);
    private static final int PARTITIONS = 1;
    private static final short REPLICATION_FACTOR = 1;

    public static void main(String[] args) {

        Properties properties = buildStreamProperties();
        createTopics(properties);

        Topology topology = A_KStreamJoinKTableTopology.buildTopology();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        try {
            kafkaStreams.start();
        } catch (Exception ex) {
            log.error("Exception in starting the Streams: {}", ex.getMessage(), ex);
        }
    }

    private static Properties buildStreamProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, );
//        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, );
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kstream_join_ktable");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        properties.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "10000");
        return properties;
    }

    private static void createTopics(Properties properties) {
        try(AdminClient adminClient = AdminClient.create(properties)) {

            Set<String> existingTopics = adminClient.listTopics().names().get();

            List<NewTopic> topicsToBeCreated = topics.stream()
                .filter(topic -> !existingTopics.contains(topic))
                .map(newTopic -> new NewTopic(newTopic, PARTITIONS, REPLICATION_FACTOR))
                .toList();

            CreateTopicsResult createTopicsResult = adminClient.createTopics(topicsToBeCreated);
            createTopicsResult.all().get();
            log.info("Topics are created successfully !!");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
