package com.jskno.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jskno.utils.RandomValueGenerator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BankTransactionProducer {

    private static final Logger log = LoggerFactory.getLogger(BankTransactionProducer.class);
    private static final ObjectMapper mapper = JsonMapper.builder()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .addModule(new JavaTimeModule()).build();

    private static final String TOPIC = "bank-transactions";
    public static final List<String> ACCOUNT_USERS = Arrays.asList("Jose", "Raquel", "Alvaro", "Eva", "Ana", "Miguel");
    private static final List<Double> ACCOUNT_BALANCE = new ArrayList<>(
        Arrays.asList(0.00, 0.00, 0.00, 0.00, 0.00, 0.00));

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, "3");
        config.put(ProducerConfig.LINGER_MS_CONFIG, "1");
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(config);

        while (true) {

            try {
                TransactionDTO transactionDTO = generateRandomTransaction();

                ProducerRecord<String, String> transactionEvent = new ProducerRecord<>(
                    TOPIC,
                    transactionDTO.getName(),
                    convertObjectToJson(transactionDTO));
                kafkaProducer.send(transactionEvent);
                Thread.sleep(100);

            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }

        }

//        kafkaProducer.flush();
        kafkaProducer.close();

        log.info("Users final balance: ");
        for (int i = 0; i < ACCOUNT_USERS.size(); i++) {
            log.info("{} final balance: {}", ACCOUNT_USERS.get(i), ACCOUNT_BALANCE.get(i));
        }

    }

    public static TransactionDTO generateRandomTransaction() {
        Double userMovement = RandomValueGenerator.getRandomPositiveDouble(0, 100, 2);
        int userIndex = RandomValueGenerator.getRandomPositiveInteger(6);
        String user = ACCOUNT_USERS.get(userIndex);

        ACCOUNT_BALANCE.set(userIndex, ACCOUNT_BALANCE.get(userIndex) + userMovement);
        TransactionDTO transactionDTO = new TransactionDTO(user, userMovement, OffsetDateTime.now());
        return transactionDTO;
    }


    public static String convertObjectToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
