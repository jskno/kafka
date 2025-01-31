package com.jskno.g_state_store;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class A0_QueryableWindowStateStoreServer {

    private final KafkaStreams kafkaStreams;
    private final String stateStoreName;

    public A0_QueryableWindowStateStoreServer(KafkaStreams kafkaStreams, String stateStoreName) {
        this.kafkaStreams = kafkaStreams;
        this.stateStoreName = stateStoreName;
    }


    // 4567 default http port
    public void start() {
        Thread thread = new Thread(() -> {
            StoreQueryParameters<ReadOnlyWindowStore<String, Long>> parameters = StoreQueryParameters
                    .fromNameAndType(stateStoreName, QueryableStoreTypes.windowStore());
            ReadOnlyWindowStore<String, Long> windowStore = kafkaStreams.store(parameters);

            spark.Spark.get("/heartbeat", (request, response) -> {
                response.type("application/json");
                ArrayList<Map<String, Object>> result = new ArrayList<>();
                windowStore.all().forEachRemaining(kv -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("key", kv.key.key());
                    map.put("start", kv.key.window().start());
                    map.put("end", kv.key.window().end());
                    map.put("value", kv.value);
                    result.add(map);
                });
                return new ObjectMapper().writeValueAsString(result);
            });
        }, "http-server-thread");
        thread.setDaemon(true);
        thread.start();
    }
}
