package com.jskno.opensearch;

import com.google.gson.JsonParser;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchConsumer_D_BatchingData {

    private final static Logger log = LoggerFactory.getLogger(OpenSearchConsumer_D_BatchingData.class.getSimpleName());

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

                BulkRequest bulkRequest = new BulkRequest();

                for (ConsumerRecord<String, String> record : consumerRecords) {
                    // Send the record into OpenSearch

                    try {
                        // we extract the id
                        String id = extractId(record.value());
                        IndexRequest indexRequest = new IndexRequest("wikimedia")
                            .source(record.value(), XContentType.JSON)
                            .id(id);

                        bulkRequest.add(indexRequest);
                    } catch (Exception e) {

                    }
                }

                if (bulkRequest.numberOfActions() > 0) {
                    BulkResponse bulk = openSearchClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                    log.info("Inserted " + bulk.getItems().length + " records");

                    // Commit offsets after the batch is consumed
                    kafkaConsumer.commitSync();
                    log.info("Offsets have been commited");

                    // This is only to allow some time for the next bulk request have actions > 0
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
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
