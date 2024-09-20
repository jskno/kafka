package com.jskno.producer.a_aggregate;

import com.jskno.topology.a_aggregate.B_ExploreCountOperatorTopologyV2;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class B_AggregateProducerV2 {

    private static KafkaProducer<String, String> kafkaProducer;

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();
        kafkaProducer = new KafkaProducer<>(properties);

        var key = "A";

        var word = "Apple";
        var word1 = "Alligator";
        var word2 = "Ambulance";

        var recordMetaData = publishMessageSync(B_ExploreCountOperatorTopologyV2.AGGREGATE, key, word);
        log.info("Published the alphabet message : {} ", recordMetaData);

        var recordMetaData1 = publishMessageSync(B_ExploreCountOperatorTopologyV2.AGGREGATE, key, word1);
        log.info("Published the alphabet message : {} ", recordMetaData1);

        var recordMetaData2 = publishMessageSync(B_ExploreCountOperatorTopologyV2.AGGREGATE, key, word2);
        log.info("Published the alphabet message : {} ", recordMetaData2);

        var bKey = "B";

        var bWord1 = "Bus";
        var bWord2 = "Baby";
        var recordMetaData3 = publishMessageSync(B_ExploreCountOperatorTopologyV2.AGGREGATE, bKey, bWord1);
        log.info("Published the alphabet message : {} ", recordMetaData3);

        var recordMetaData4 = publishMessageSync(B_ExploreCountOperatorTopologyV2.AGGREGATE, bKey, bWord2);
        log.info("Published the alphabet message : {} ", recordMetaData4);
    }

    private static Properties buildProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }


    private static Object publishMessageSync(String topic, String key, String value) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);
        try {
            log.info("Producer Record: " + producerRecord);
            return kafkaProducer.send(producerRecord).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
