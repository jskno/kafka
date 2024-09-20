package com.jskno.greetings.producer;

import com.jskno.greetings.constants.GreetingType;
import com.jskno.greetings.constants.GreetingsConstants;
import com.jskno.greetings.domain.Greeting;
import com.jskno.greetings.domain.Pair;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Slf4j
public class B_JsonGreetingMockDataProducer {

    private static final KafkaProducer<String, Greeting> kafkaProducer = new KafkaProducer<>(producerProps());

    private static final List<Pair<GreetingType, String>> GREETINGS = List.of(
        new Pair<>(GreetingType.MORNING, "How are you?"),
        new Pair<>(GreetingType.AFTERNOON, "How do you do?"),
        new Pair<>(GreetingType.EVENING, "TransientError"),
        new Pair<>(GreetingType.MORNING, "Are you OK?"),
        new Pair<>(GreetingType.EVENING, "Everything is gonna be allright")
    );


    public static void main(String[] args) {
        GREETINGS.forEach(greetingPair -> {
            Greeting greeting = new  Greeting(greetingPair.getL(), greetingPair.getR(), OffsetDateTime.now());
            publishMessage(greeting);
        });
    }

    private static void publishMessage(Greeting greeting) {
        ProducerRecord<String, Greeting> producerRecord = buildProducerRecord(greeting);
        try {
            kafkaProducer.send(producerRecord).get();
            log.info("producerRecord : " + producerRecord);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception in  publishMessageSync : {}  ", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static ProducerRecord<String, Greeting> buildProducerRecord(Greeting greeting) {
        return new ProducerRecord<>(GreetingsConstants.JSON_GREETINGS, greeting.type().name(), greeting);
    }

    private static Properties producerProps() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return properties;
    }

}
