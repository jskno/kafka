package com.jskno.producer;

import com.jskno.domain.Greeting;
import com.jskno.serdes.harcoded.GreetingSerializer;
import com.jskno.topology.kstream.GreetingsTopologyV3;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class GreetingMockDataProducerV8 {

    private static final List<String> GREETINGS = List.of(
        "How are you?",
        "How do you do?",
        "TransientError",
        "Are you OK?",
        "Everything is gonna be allright"
    );

    private static final List<String> SPA_GREETINGS = List.of(
        "Cómo estás?",
        "Cómo está usted?",
        "Qué ahí?",
        "Estás bien?",
        "Todo va a salir bien"
    );

    public static void main(String[] args) {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GreetingSerializer.class.getName());

        KafkaProducer<String, Greeting> kafkaProducer = new KafkaProducer<>(properties);

        IntStream.range(0, 5).forEach(value ->
            kafkaProducer.send(buildRecord(GreetingsTopologyV3.GREETINGS, "m" + value, GREETINGS.get(value))));

        IntStream.range(0, 5).forEach(value ->
            kafkaProducer.send(buildRecord(GreetingsTopologyV3.GREETINGS_SPANISH, "e" + value, SPA_GREETINGS.get(value))));

        // Flush and close the Producer - synchronous
        // Tell the producer to send all data and block until done -- synchronous
        kafkaProducer.flush();
        // Close the producer
        kafkaProducer.close();

    }

    private static ProducerRecord<String, Greeting> buildRecord(String topic, String key, String message) {
        return new ProducerRecord<>(
            topic,
            key,
            new Greeting(message, OffsetDateTime.now()));
    }

}
