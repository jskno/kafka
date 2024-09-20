package com.jskno.producer;

import static com.jskno.topology.ktable.ExploreKTableTopology.WORDS;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class WordsProducer {

    private static KafkaProducer<String, String> kafkaProducer;

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        kafkaProducer = new KafkaProducer<>(properties);

        var key = "A";

        var word = "Apple";
        var word1 = "Alligator";
//        var word2 = "Ambulance3";

        var recordMetaData = publishMessageSync(WORDS, key, word);
        log.info("Published the alphabet message : {} ", recordMetaData);

        var recordMetaData1 = publishMessageSync(WORDS, key, word1);
        log.info("Published the alphabet message : {} ", recordMetaData1);

//        var recordMetaData2 = publishMessageSync(WORDS, key, word2);
//        log.info("Published the alphabet message : {} ", recordMetaData2);

        var bKey = "B";

        var bWord1 = "Bus";
        var bWord2 = "Baby2";
        var recordMetaData3 = publishMessageSync(WORDS, bKey, bWord1);
        log.info("Published the alphabet message : {} ", recordMetaData3);

        var recordMetaData4 = publishMessageSync(WORDS, bKey, bWord2);
        log.info("Published the alphabet message : {} ", recordMetaData4);

    }

    private static RecordMetadata publishMessageSync(String topic, String key, String value) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
        RecordMetadata recordMetadata = null;

        try {
            log.info("Producer Record: " + producerRecord);
            recordMetadata = kafkaProducer.send(producerRecord).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return recordMetadata;
}


}
