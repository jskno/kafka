package com.jskno.producer;

import static com.jskno.constants.Kafka101Constants.PURCHASES_TOPIC;

import java.util.Properties;
import java.util.Random;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;

public class ProducerExample {

    public static void main(String[] args) {
        final Properties properties = buildProducerProperties();

        String[] users = {"eabara", "jsmith", "sgarcia", "jbernard", "htanaka", "awalther"};
        String[] items = {"book", "alarm clock", "t-shirts", "gift card", "batteries"};

        try(final Producer<String, String> producer = new KafkaProducer<>(properties)) {
            final Random random = new Random();
            final int numMessages = 10;

            for(int i = 0; i < numMessages; i++) {
                String user = users[random.nextInt(users.length)];
                String item = items[random.nextInt(items.length)];

                producer.send(
                    new ProducerRecord<>(PURCHASES_TOPIC, user, item),
                    (recordMetadata, ex) -> {
                        if (ex != null) {
                            ex.printStackTrace();
                        } else {
                            System.out.printf("Produced event to topic %s: key = %-10s value = %s%n", PURCHASES_TOPIC, user, item);
                        }
                    });
            }

            System.out.printf("%s events were produced to topic %s%n", numMessages, PURCHASES_TOPIC);

        }
    }

    private static Properties buildProducerProperties() {
        var key = "WTHUTRM57A2FQF7S";
        var secret = "/4YM6KP2v+D5bY3Agio3hTiyy/PlDV72Asd/GeNNETr+NKRPZmfkb9x8fe3yQfn/";

        return new Properties() {{
            // User-specific or environment-specific properties that you must set
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "pkc-12576z.us-west2.gcp.confluent.cloud:9092");
            put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username='WTHUTRM57A2FQF7S' password='/4YM6KP2v+D5bY3Agio3hTiyy/PlDV72Asd/GeNNETr+NKRPZmfkb9x8fe3yQfn/';");

            // Fixed properties
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getCanonicalName());
            put(ProducerConfig.ACKS_CONFIG, "all");
            put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
            put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        }};
    }

}
