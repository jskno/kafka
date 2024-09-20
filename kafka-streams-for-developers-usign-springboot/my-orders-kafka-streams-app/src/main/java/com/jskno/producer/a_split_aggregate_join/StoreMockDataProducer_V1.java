package com.jskno.producer.a_split_aggregate_join;

import com.jskno.domain.store.Address;
import com.jskno.domain.store.Store;
import com.jskno.serdes.JsonSerializer;
import com.jskno.topology.c_ktable_join.OrderTopologyV5;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class StoreMockDataProducer_V1 {

    private static KafkaProducer<String, Store> kafkaProducer;

    // Publish Messages Sync
    public static void main(String[] args) {

        var address1 = new Address("1234 Street 1 ", "", "City1", "State1", "12345");
        var store1 = new Store("location1",
            address1,
            "1234567890"
        );

        var address2 = new Address("1234 Street 2 ", "", "City2", "State2", "541321");
        var store2 = new Store("location2",
            address2,
            "0987654321"
        );


        var stores = List.of(store1, store2);

        stores.forEach(store -> {
            var storeRecord = buildStoreRecord(store);
            publishMessageSync(storeRecord);
        });
    }

    private static RecordMetadata publishMessageSync(ProducerRecord<String, Store> producerRecord) {
        if (kafkaProducer == null) {
            createKafkaProducer();
        }

        try {
            RecordMetadata recordMetadata = kafkaProducer.send(producerRecord).get();
            log.info("Published the store message : {} ", recordMetadata);
            return recordMetadata;
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception in publishMessageSync: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static void createKafkaProducer() {
        Properties properties = createProducerProperties();
        kafkaProducer = new KafkaProducer<>(properties);
    }

    private static Properties createProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }

    private static ProducerRecord<String, Store> buildStoreRecord(Store store) {
        return new ProducerRecord<>(OrderTopologyV5.STORES, store.locationId(), store);
    }


}
