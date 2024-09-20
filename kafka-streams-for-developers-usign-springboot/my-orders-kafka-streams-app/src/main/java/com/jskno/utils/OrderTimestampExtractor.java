package com.jskno.utils;

import com.jskno.domain.order.Order;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

@Slf4j
public class OrderTimestampExtractor implements TimestampExtractor {

    @Override
    public long extract(ConsumerRecord<Object, Object> consumerRecord, long partitionTime) {
        var order = (Order) consumerRecord.value();
        if (order != null && order.orderDateTime() != null) {
            var timestamp = order.orderDateTime();
            log.info("Timestamp in extractor: {}", timestamp);
            return convertToInstant(timestamp);
        }
        return 0;
    }

    private long convertToInstant(OffsetDateTime timestamp) {
        return timestamp.toInstant().toEpochMilli();
    }
}
