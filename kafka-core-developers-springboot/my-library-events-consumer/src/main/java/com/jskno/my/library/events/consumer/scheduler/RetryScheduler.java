package com.jskno.my.library.events.consumer.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jskno.my.library.events.consumer.config.LibraryEventsConsumerConfig;
import com.jskno.my.library.events.consumer.entity.FailureRecord;
import com.jskno.my.library.events.consumer.jpa.FailureRecordRepository;
import com.jskno.my.library.events.consumer.service.LibraryEventsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hibernate.validator.internal.util.logging.Log;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RetryScheduler {

    private final FailureRecordRepository failureRecordRepository;
    private final LibraryEventsService libraryEventsService;

    @Scheduled(fixedRate = 10000)
    public void retryFailedRecords() {
        log.info("Retrying Failed Records Started !");
        failureRecordRepository.findAllByStatus(LibraryEventsConsumerConfig.RETRY)
            .forEach(failureRecord -> {
                log.info("Retrying Failed Record: {}", failureRecord);
                ConsumerRecord<Integer, String> consumerRecord = buildConsumerRecord(failureRecord);
                try {
                    libraryEventsService.processLibraryEvent(consumerRecord);
                    failureRecord.setStatus(LibraryEventsConsumerConfig.SUCCESS);
                } catch (Exception e) {
                    log.error("Exception in retryFailedRecords: {}", e.getMessage(), e);
//                    throw new RuntimeException(e);
                }
            });
        log.info("Retrying Failed Records Completed !");
    }

    private ConsumerRecord<Integer, String> buildConsumerRecord(FailureRecord failureRecord) {
        return new ConsumerRecord<>(
            failureRecord.getTopic(),
            failureRecord.getPartition(),
            failureRecord.getOffsetValue(),
            failureRecord.getTopicKey(),
            failureRecord.getErrorRecord()
        );
    }

}
