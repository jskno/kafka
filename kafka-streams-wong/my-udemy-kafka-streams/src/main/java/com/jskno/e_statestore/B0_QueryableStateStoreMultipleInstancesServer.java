package com.jskno.e_statestore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.d_stateful_aggregation.model.SalesStats;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import java.util.ArrayList;

public class B0_QueryableStateStoreMultipleInstancesServer {

    private final KafkaStreams kafkaStreams;
    private final String stateStoreName;

    public B0_QueryableStateStoreMultipleInstancesServer(KafkaStreams kafkaStreams, String stateStoreName) {
        this.kafkaStreams = kafkaStreams;
        this.stateStoreName = stateStoreName;
    }

    // 4567 default http port
    public void start() {
        spark.Spark.port(Integer.parseInt(System.getProperty("port")));
        Thread thread = new Thread(() -> {
            StoreQueryParameters<ReadOnlyKeyValueStore<String, SalesStats>> parameters = StoreQueryParameters
                    .fromNameAndType(stateStoreName, QueryableStoreTypes.keyValueStore());
            ReadOnlyKeyValueStore<String, SalesStats> keyValueStore = kafkaStreams.store(parameters);
            spark.Spark.get("/sales-stats", (request, response) -> {
                response.type("application/json");
                ArrayList<SalesStats> result = new ArrayList<>();
                keyValueStore.all().forEachRemaining(e -> result.add(e.value));
                return new ObjectMapper().writeValueAsString(result);
            });
        }, "http-server-thread");
        thread.setDaemon(true);
        thread.start();
    }
}
