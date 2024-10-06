package com.jskno.producer.e_my_exercise;

import com.jskno.constants.OrdersConstants;
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
public class A_StoreProducerJoin {

    private static KafkaProducer<String, Store> kafkaProducer;

    // Publish Messages Sync
    public static void main(String[] args) {

        Properties properties = buildProducerProperties();
        try(KafkaProducer<String, Store> kafkaProducer = new KafkaProducer<>(properties)) {
            List<Store> stores = buildStores();
            List<ProducerRecord<String, Store>> producerRecords = buildStoreRecord(stores);

            producerRecords.forEach(record -> {
                try {
                    RecordMetadata recordMetadata = kafkaProducer.send(record).get();
                    log.info("Published the store message : {} ", recordMetadata);
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Exception in publishMessageSync: {}", e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static Properties buildProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }

    private static List<Store> buildStores() {
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

        // No Orders from this address
        var address3 = new Address("8942 Street 25", "", "City25", "State25", "252525");
        var store3 = new Store("location3",
            address3,
            "630050505"
        );

        return List.of(store1, store2, store3);
    }

    private static List<ProducerRecord<String, Store>> buildStoreRecord(List<Store> stores) {
        return stores.stream().map(store -> new ProducerRecord<>(
            OrdersConstants.STORES_TOPIC, store.locationId(), store))
            .toList();
    }

}
