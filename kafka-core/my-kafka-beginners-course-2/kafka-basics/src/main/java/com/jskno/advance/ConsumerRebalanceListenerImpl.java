package com.jskno.advance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerRebalanceListenerImpl implements ConsumerRebalanceListener {

    private static final Logger log = LoggerFactory.getLogger(ConsumerRebalanceListenerImpl.class);

    private final KafkaConsumer<String, String> consumer;
    private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public ConsumerRebalanceListenerImpl(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        log.info("onPartitionsRevoked callback triggered");
        log.info("Committing offsets: " + currentOffsets);

        consumer.commitSync(currentOffsets);

    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        log.info("onPartitionsAssigned callback triggered");
    }

    public void addOffsetToTrack(String topic, int partition, long offset) {
        currentOffsets.put(
            new TopicPartition(topic, partition),
            new OffsetAndMetadata(offset + 1, null));
    }

    public Map<TopicPartition, OffsetAndMetadata> getCurrentOffsets() {
        return currentOffsets;
    }
}
