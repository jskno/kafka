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
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSearchConsumer {

    private final static Logger log = LoggerFactory.getLogger(OpenSearchConsumer.class.getSimpleName());

    public static void main(String[] args) throws IOException {

        // First create OpenSearch client
        RestHighLevelClient openSearchClient = OpenSearchClientUtils.createOpenSearchClient();

        // Second create out Kafka Client (Consumer)
        KafkaConsumer<String, String> kafkaConsumer = KafkaConsumerUtils.createKafkaConsumer();

        KafkaConsumerUtils.addShutdownHook(kafkaConsumer);

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

                    // strategy 1
                    // define on ID using Kafka Record coordinates
//                    String id = record.topic() + "_" +record.partition() + "_"+ record.offset();

                    try {
                        // strategy 2
                        // we extract the id
                        String id = extractId(record.value());
                        IndexRequest indexRequest = new IndexRequest("wikimedia")
                            .source(record.value(), XContentType.JSON)
                            .id(id);

//                        IndexResponse response = openSearchClient.index(indexRequest, RequestOptions.DEFAULT);
//                        log.info("Inserted 1 document into OpenSearch with id {}", response.getId());
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
            openSearchClient.close();
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
