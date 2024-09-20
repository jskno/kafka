package com.jskno.opensearch;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchConsumer_C_CommitManually {

    private final static Logger log = LoggerFactory.getLogger(OpenSearchConsumer_C_CommitManually.class.getSimpleName());

    public static void main(String[] args) {

        // First create OpenSearch client
        RestHighLevelClient openSearchClient = OpenSearchClientUtils.createOpenSearchClient();

        // Second create our Kafka Client (Consumer)
        KafkaConsumer<String, String> kafkaConsumer = KafkaConsumerUtils.createKafkaConsumer("false");
        KafkaConsumerUtils.addShutdownHook(kafkaConsumer);

        // Main code logic
        try (openSearchClient; kafkaConsumer) {

            // We need to create the index openSearch if it doesn't exist already
            OpenSearchClientUtils.createWikimediaIndex(openSearchClient);

            kafkaConsumer.subscribe(Collections.singleton("wikimedia.recentchange"));

            while (true) {

                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(3000));
                int recordsCount = consumerRecords.count();
                log.info("Received {} record(s)", recordsCount);

                for (ConsumerRecord<String, String> record : consumerRecords) {
                    // Send the record into OpenSearch

                    // IF WE ADD AN ID TO THE INDEX, THE OPENSEARCH DATABASE WILL NEVER INSERT DUPLICATED DATA
                    // IT CHECKS WHETHER OR NOT THIS INDEX EXISTS AND IF IT DOES, IT WILL UPDATE THE DATA
                    // DOING THAT WE MAKE SURE OUR CONSUMER IS IDEMPOTENT, SO IN OUT SCENARIO
                    // KAFKA -> EXTERNAL APP, WE HAVE THE AT LEAST ONCE SEMANTICS
                    // AND WE MUST END UP PROCESSING SAME KAFKA EVENT TWICE, SO WE MUST ALWAYS MAKE SURE
                    // OUR CONSUMER IS IDEMPOTENT, AND IT DOES NOT AFFECT US

                    // strategy 1
                    // define an ID using Kafka Record coordinates
//                    String id = record.topic() + "_" +record.partition() + "_"+ record.offset();

                    try {
                        // strategy 2
                        // we extract the id
                        String id = extractId(record.value());
                        IndexRequest indexRequest = new IndexRequest("wikimedia")
                            .source(record.value(), XContentType.JSON)
                            .id(id);

                        IndexResponse response = openSearchClient.index(indexRequest, RequestOptions.DEFAULT);
                        log.info("Inserted 1 document into OpenSearch with id {}", response.getId());
                    } catch (Exception e) {

                    }
                }

                // Commit Offsets after the batch of records is consumed
                kafkaConsumer.commitSync();
                log.info("Offsets have been commited");
            }
        } catch (WakeupException ex) {
            // We ignore this as this is an expected exception when closing a consumer
            log.info("Wake up exception");
        } catch (Exception ex) {
            log.error("Unexpected exception");
        } finally {
            // Close things
            kafkaConsumer.close(); // This will also commit the offsets if need be
            try {
                openSearchClient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("The consumer is now gracefully closed");
        }

    }

    private static String extractId(String json) {
        return JsonParser.parseString(json)
            .getAsJsonObject()
            .get("meta")
            .getAsJsonObject()
            .get("id")
            .getAsString();
    }

}
