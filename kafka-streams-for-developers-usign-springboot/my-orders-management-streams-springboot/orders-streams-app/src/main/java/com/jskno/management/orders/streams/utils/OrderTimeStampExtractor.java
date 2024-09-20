package com.jskno.management.orders.streams.utils;

import com.jskno.management.orders.domain.order.Order;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

@Slf4j
public class OrderTimeStampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        var orderRecord = (Order) record.value();
        if(orderRecord!=null && orderRecord.orderDateTime()!=null){
            var timeStamp = orderRecord.orderDateTime();
            log.info("TimeStamp in extractor : {} ", timeStamp);
            return convertToInstantUTCFromCST(timeStamp);
        }
        //fallback to stream time
        return partitionTime;
    }

    private static long convertToInstantUTCFromCST(OffsetDateTime timeStamp) {
        var instant = timeStamp.toInstant().toEpochMilli();
        log.info("instant in extractor : {} ", instant);
        return instant;
    }

    /**
     * Use this if the passed in time is also in UTC.
     * @param timeStamp
     * @return
     */
    private static long convertToInstantUTC(LocalDateTime timeStamp) {
        var instant = timeStamp.toInstant(ZoneOffset.UTC).toEpochMilli();
        log.info("instant in extractor : {} ", instant);
        return instant;
    }
}
