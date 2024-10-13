package com.jskno.consumer;

import static com.jskno.constants.Kafka101Constants.PURCHASES_TOPIC;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;

public class ConsumerExample {

    public static void main(String[] args) {
        Properties properties = buildConsumerProperties();

        try(final Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
           consumer.subscribe(List.of(PURCHASES_TOPIC));
           while(true) {
               ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
               for (ConsumerRecord<String, String> record : records) {
                   String key = record.key();
                   String value = record.value();
                   System.out.printf("Consumed event from topic %s: key = %-10s value = %s%n", PURCHASES_TOPIC, key, value);
               }
           }
        }
    }

    private static Properties buildConsumerProperties() {
        return new Properties() {{
            // User-specific or environment-specific properties that you must set
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-12576z.us-west2.gcp.confluent.cloud:9092");
            put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username='WTHUTRM57A2FQF7S' password='/4YM6KP2v+D5bY3Agio3hTiyy/PlDV72Asd/GeNNETr+NKRPZmfkb9x8fe3yQfn/';");

            // Fixed properties
            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());
            put(ConsumerConfig.GROUP_ID_CONFIG, "kafka-101");
            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }};
    }

}
