package com.jskno.my.library.events.consumer.service;

import com.jskno.my.library.events.consumer.entity.FailureRecord;
import com.jskno.my.library.events.consumer.jpa.FailureRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FailureService {

    private final FailureRecordRepository failureRecordRepository;

    public void saveFailedRecord(ConsumerRecord<Integer,String> consumerRecord, Exception e, String status) {
        FailureRecord failureRecord = FailureRecord.builder()
            .topic(consumerRecord.topic())
            .topicKey(consumerRecord.key())
            .errorRecord(consumerRecord.value())
            .partition(consumerRecord.partition())
            .offsetValue(consumerRecord.offset())
            .exception(e.getCause().getMessage())
            .status(status)
            .build();

        failureRecordRepository.save(failureRecord);
    }
}
