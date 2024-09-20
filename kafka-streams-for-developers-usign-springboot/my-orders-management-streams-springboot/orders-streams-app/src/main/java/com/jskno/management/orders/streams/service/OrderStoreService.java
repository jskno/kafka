package com.jskno.management.orders.streams.service;

import com.jskno.management.orders.domain.revenue.TotalRevenue;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStoreService {

    private final StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    public ReadOnlyKeyValueStore<String, Long> ordersCountStore(String storeName) {
        return Objects.requireNonNull(streamsBuilderFactoryBean.getKafkaStreams())
            .store(StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));

    }

    public ReadOnlyKeyValueStore<String, TotalRevenue> ordersRevenueStore(String storeName) {
        return Objects.requireNonNull(streamsBuilderFactoryBean.getKafkaStreams())
            .store(StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));
    }

    public ReadOnlyWindowStore<String, Long> orderWindowCountStore(String storeName) {
        return Objects.requireNonNull(streamsBuilderFactoryBean.getKafkaStreams())
            .store(StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.windowStore()));
    }

    public ReadOnlyWindowStore<String, TotalRevenue> getOrderWindowRevenueStore(String storeName) {
        return Objects.requireNonNull(streamsBuilderFactoryBean.getKafkaStreams())
            .store(StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.windowStore()));
    }
}
