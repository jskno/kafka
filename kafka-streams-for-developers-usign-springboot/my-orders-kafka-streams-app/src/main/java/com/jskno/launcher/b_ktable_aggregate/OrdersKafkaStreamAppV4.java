package com.jskno.launcher.b_ktable_aggregate;

import com.jskno.topology.a_kstream_split.OrderTopology;
import com.jskno.topology.b_ktable_aggregate.OrderTopologyV4;
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
public class OrdersKafkaStreamAppV4 {

    private static final int PARTITIONS = 1;
    private static final short REPLICATION_FACTOR = 1;

    private static final Set<String> topics = Set.of(
        OrderTopology.ORDERS,
        OrderTopology.GENERAL_ORDERS,
        OrderTopology.RESTAURANT_ORDERS,
        OrderTopology.STORES
    );

    public static void main(String[] args) {

        Properties properties = buildProperties();
        createTopics(properties);
        Topology topology = OrderTopologyV4.buildTopology();

        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);

        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
        try {
            kafkaStreams.start();
        } catch (Exception ex) {
            log.error("Exception in starting the Streams: {}", ex.getMessage(), ex);
        }
    }

    private static Properties buildProperties() {
        var properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "orders-app");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
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
