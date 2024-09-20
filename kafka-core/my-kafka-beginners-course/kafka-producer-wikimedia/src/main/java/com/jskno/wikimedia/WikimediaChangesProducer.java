package com.jskno.wikimedia;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class WikimediaChangesProducer {

    public static void main(String[] args) throws InterruptedException {

//        KafkaTopicFactory.createTopic();

        String bootstrapServers = "127.0.0.1:9092";

        // create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Set safe producer configs (Kafka <= 2.8) IDEMPOTENT PRODUCER
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG , "true");
        // Make sure this other 4 settings are correctly set automatically otherwise do it manually
//        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
//        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE)); // retry until delivery.timeout.ms is reached
//        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); RETRIES will keep ordering
//        properties.setProperty(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "120000");
        // The topic should have a replication factor of at least 3 and the min insync of 2
        // replication.factor = 3
        // min.insync.replicas = 2 (broker/topic level)
//        properties.setProperty(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, "2");

        // create the Producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        String topic = "wikimedia.recentchange";

        EventHandler eventHandler = new WikimediaChangeHandler(producer, topic);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();

        // Start the producer in another thread
        eventSource.start();

        // we produce for 10 min and block the program until then
        TimeUnit.MINUTES.sleep(10);

    }

}
