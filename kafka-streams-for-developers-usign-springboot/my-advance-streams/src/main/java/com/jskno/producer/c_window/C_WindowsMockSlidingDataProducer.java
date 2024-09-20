package com.jskno.producer.c_window;

import static com.jskno.topology.c_window.A_ExploreCountTumblingWindowTopology.WINDOW_WORDS;
import static java.lang.Thread.sleep;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class C_WindowsMockSlidingDataProducer {

    private static KafkaProducer<String, String> kafkaProducer;

    public static void main(String[] args) throws InterruptedException {

        var key = "A";
        var word = "Apple";
        int count = 0;
        while(count<10){
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(WINDOW_WORDS, key, word);
            publishMessageSync(producerRecord);
            sleep(1000);
            count++;
        }
    }

    private static RecordMetadata publishMessageSync(ProducerRecord<String, String> producerRecord) {
        if (kafkaProducer == null) {
            createKafkaProducer();
        }

        try {
            RecordMetadata recordMetadata = kafkaProducer.send(producerRecord).get();
            log.info("Published the word windowed message : {} ", recordMetadata);
            return recordMetadata;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception in publishMessageSync: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static void createKafkaProducer() {
        Properties properties = buildProducerProperties();
        kafkaProducer = new KafkaProducer<>(properties);
    }

    private static Properties buildProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }

}
