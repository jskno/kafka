package com.jskno.b_stateful_app.processor;

import com.jskno.b_stateful_app.A4_WordCountApp;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;

public class CustomProcessor implements Processor<String, String, String, Integer> {

    private ProcessorContext<String, Integer> context;
    private KeyValueStore<String, Integer> store;

    @Override
    public void init(ProcessorContext<String, Integer> context) {
        this.context = context;
        store = context.getStateStore(A4_WordCountApp.STATE_STORE_NAME);
        this.context.schedule(Duration.ofSeconds(30), PunctuationType.STREAM_TIME, this::forwardAll);
    }

    private void forwardAll(final long timestamp) {
        try (KeyValueIterator<String, Integer> iterator = store.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, Integer> nextKV = iterator.next();
                final Record<String, Integer> countWord = new Record<>(nextKV.key, nextKV.value, timestamp);
                context.forward(countWord);
            }
        }
    }

    @Override
    public void process(Record<String, String> record) {
        Integer count = store.get(record.key());
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        store.put(record.key(), count);
    }

    @Override
    public void close() {
    }
}