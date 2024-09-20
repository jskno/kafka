package com.jskno.greetings.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

@Slf4j
public class C_WrongGreetingMockDataProducer {

    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    private static final KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerProps());

    private static final List<String> GREETINGS_ERRORS = List.of(
        "HowAreYou?",
        "TransientError",
        "NowStopApp"
    );

    private static final List<Pair<GreetingType, String>> GREETINGS = List.of(
        new Pair<>(GreetingType.MORNING, "HowAreYou?"),
        new Pair<>(GreetingType.AFTERNOON, "How do you do?"),
        new Pair<>(GreetingType.EVENING, "TransientError"),
        new Pair<>(GreetingType.MORNING, "Are you OK?"),
        new Pair<>(GreetingType.MORNING, "NowStopApp"),
        new Pair<>(GreetingType.EVENING, "Everything is gonna be allright")
    );


    // We are here sending wrong data to JSON_GREETING topic
    // The topic expects as value: {"message": "Hi there!", "dateTime":"2024-08-27T10:05:35+02:00"}
    // We are sending as value: "Hi there!"

    // Error logs:
    // Caused by: org.apache.kafka.common.errors.SerializationException: Can't deserialize data  from topic [json-greetings]
    // Caused by: com.fasterxml.jackson.core.JsonParseException: Unrecognized token 'How': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')
    // State transition from PENDING_ERROR to ERROR

    // This wrong message format causes the application shut down
    // efault.deserialization.exception.handler = class org.apache.kafka.streams.errors.LogAndFailExceptionHandler
    // Sth we must avoid, the correct behaviour would be log the error, maybe store it, sentry alert and skip the record
    // to keep processing the next ones

    // Handle Deserialization Error:
    // Approach1: default.deserialization.exception.handler = class org.apache.kafka.streams.errors.LogAndFailExceptionHandler
    // Approach2: using custom error handler
    // Approach3: using Spring specific approach
    public static void main(String[] args) {
        GREETINGS.forEach(greetingPair -> {
            Greeting greeting = new  Greeting(greetingPair.getL(), greetingPair.getR(), OffsetDateTime.now());
            publishMessage(greeting);
        });
    }

    private static void publishMessage(Greeting greeting) {
        ProducerRecord<String, String> producerRecord = buildProducerRecord(greeting);
        try {
            kafkaProducer.send(producerRecord).get();
            log.info("producerRecord : " + producerRecord);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception in  publishMessageSync : {}  ", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static ProducerRecord<String, String> buildProducerRecord(Greeting greeting) {
        if (GREETINGS_ERRORS.contains(greeting.message())) {
            return new ProducerRecord<>(
                GreetingsConstants.JSON_GREETINGS,
                greeting.type().name(),
                greeting.message());
        } else {
            return buildRightProducerRecord(greeting);
        }
    }

    private static ProducerRecord<String, String> buildRightProducerRecord(Greeting greeting) {
        try {
            var greetingJSON = objectMapper.writeValueAsString(greeting);
            return new ProducerRecord<>(
                GreetingsConstants.JSON_GREETINGS,
                greeting.type().name(),
                greetingJSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Properties producerProps() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }

}
