package com.jskno.b_stateful_app.processor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Collections;
import java.util.Set;

public class CustomProcessorSupplier implements ProcessorSupplier<String, String, String, Integer> {

    private final String storeName;

    private final StoreBuilder<KeyValueStore<String, Integer>> keyValueStoreBuilder;

    public CustomProcessorSupplier(String storeName) {
        this.storeName = storeName;
        this.keyValueStoreBuilder = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(storeName), Serdes.String(), Serdes.Integer());
    }

    @Override
    public Processor<String, String, String, Integer> get() {
        return new CustomProcessor();
    }

    @Override
    public Set<StoreBuilder<?>> stores() {
        return Collections.singleton(keyValueStoreBuilder);
    }
}
