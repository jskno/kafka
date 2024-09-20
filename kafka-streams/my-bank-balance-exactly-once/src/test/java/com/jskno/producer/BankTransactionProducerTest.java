package com.jskno.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BankTransactionProducerTest {

    @Test
    void test() {
        TransactionDTO transactionDTO = BankTransactionProducer.generateRandomTransaction();

        ProducerRecord<String, String> transactionEvent = new ProducerRecord<>(
            "bank-transactions",
            transactionDTO.getName(),
            BankTransactionProducer.convertObjectToJson(transactionDTO));

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(transactionEvent.value());
            Assertions.assertTrue(BankTransactionProducer.ACCOUNT_USERS.contains(node.get("name").asText()), "The user is not recognized");
            Assertions.assertTrue(node.get("amount").asInt() < 100, "Amount should be less than 100");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(transactionEvent.value());
    }

}
