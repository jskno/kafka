package com.jskno.wikimedia;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;

public class KafkaTopicFactory {

    private static final String bootstrapServers = "127.0.0.1:9092";
    private static final String topicName = "wikimedia.recentchange";

    public static final void createTopic() {
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        Admin admin = Admin.create(properties);

        try (admin) {
            int partitions = 1;
            short replicationFactor = 1;
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            CreateTopicsResult result = admin.createTopics(
                Collections.singleton(newTopic)
            );

            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
