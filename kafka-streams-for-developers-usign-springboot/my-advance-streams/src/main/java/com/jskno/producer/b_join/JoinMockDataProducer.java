package com.jskno.producer.b_join;

import static com.jskno.topology.b_join.D_ExploreKStreamJoinKStreamTopology.ALPHABETS;

import com.jskno.topology.b_join.A_ExploreKStreamJoinKTableTopology;
import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class JoinMockDataProducer {

    private static KafkaProducer<String, String> kafkaProducer;

    public static void main(String[] args) {

        // KSTREAM JOIN KTABLE
        // KSTREAM JOIN GLOBALKTABLE

        // NEW EVENTS INTO THE KTABLE DOES NOT TRIGGER ANY JOIN
        // BUT WHEN LATER ON NEW EVENTS ARE ADDED INTO THE KSTREAM THERE WILL BE
        // A NEW JOIN WITH THOSE LAST VALUES ADDED FOR THE KTABLE.
        // SO THE TRIGGER THAT MAKES UPDATE THE alphabets-with-abbreviation IT IS ONLY KSTREAM
        // AND RETRIEVE FROM THE KTABLE THE MOST UP TO DATE DATA.

        // KTABLE JOIN KTABLE
        // THIS IS SOLVED IN THE CASE OF KTABLE JOIN KTABLE

        var alphabetMap = Map.of(
            "A", "A is the first letter in English Alphabets.",
            "B", "B is the second letter in English Alphabets."
        );
        var alphRecords = alphabetMap.entrySet().stream()
            .map(entry -> new ProducerRecord<>(ALPHABETS, entry.getKey(), entry.getValue()))
            .toList();
        alphRecords.forEach(JoinMockDataProducer::publishMessageSync);

        alphRecords = alphabetMap.entrySet().stream()
            .map(entry -> new ProducerRecord<>(
                ALPHABETS,
                0,
                Instant.now().plusSeconds(5).toEpochMilli(),
                entry.getKey(),
                entry.getValue()))
            .toList();
//        alphRecords.forEach(JoinMockDataProducer::publishMessageSync);

        var alphabetAbbrevationMap = Map.of(
            "A", "Apple",
            "B", "Bus"
            , "C", "Cat"

        );
        var abbrevRecords = alphabetAbbrevationMap.entrySet().stream()
            .map(entry -> new ProducerRecord<>(A_ExploreKStreamJoinKTableTopology.ALPHABETS_ABBREVIATIONS, entry.getKey(), entry.getValue()))
            .toList();
        abbrevRecords.forEach(JoinMockDataProducer::publishMessageSync);

        alphabetAbbrevationMap = Map.of(
            "A", "Airplane",
            "B", "Baby."

        );
        abbrevRecords = alphabetAbbrevationMap.entrySet().stream()
            .map(entry -> new ProducerRecord<>(A_ExploreKStreamJoinKTableTopology.ALPHABETS_ABBREVIATIONS, entry.getKey(), entry.getValue()))
            .toList();
//        abbrevRecords.forEach(JoinMockDataProducer::publishMessageSync);

        alphabetMap = Map.of(
            "E", "E is the fifth letter in English Alphabets.",
            "A", "A is the First letter in English Alphabets.",
            "B", "B is the Second letter in English Alphabets."
        );
        alphRecords = alphabetMap.entrySet().stream()
            .map(entry -> new ProducerRecord<>(A_ExploreKStreamJoinKTableTopology.ALPHABETS, entry.getKey(), entry.getValue()))
            .toList();
//        alphRecords.forEach(JoinMockDataProducer::publishMessageSync);

        alphabetAbbrevationMap = Map.of(
            "A", "Abstract",
            "B", "Babilonia.",
            "E", "Expectations"
        );
        abbrevRecords = alphabetAbbrevationMap.entrySet().stream()
            .map(entry -> new ProducerRecord<>(A_ExploreKStreamJoinKTableTopology.ALPHABETS_ABBREVIATIONS, entry.getKey(), entry.getValue()))
            .toList();
//        abbrevRecords.forEach(JoinMockDataProducer::publishMessageSync);
    }

    private static RecordMetadata publishMessageSync(ProducerRecord<String, String> producerRecord) {
        if (kafkaProducer == null) {
            createKafkaProducer();
        }

        try {
            return kafkaProducer.send(producerRecord).get();
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
