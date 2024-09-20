package com.jskno.exceptionhandler;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.errors.DeserializationExceptionHandler;
import org.apache.kafka.streams.processor.ProcessorContext;

@Slf4j
public class StreamsDeserializationExceptionHandler implements DeserializationExceptionHandler {

    int errorCounter = 0;

    @Override
    public DeserializationHandlerResponse handle(ProcessorContext processorContext,
        ConsumerRecord<byte[], byte[]> consumerRecord,
        Exception e) {
        log.info("Exception: {}, record: {}, errorCounter: {}",
            e.getMessage(), consumerRecord, errorCounter, e);
        if (errorCounter < 2) {
            errorCounter++;
            return DeserializationHandlerResponse.CONTINUE;
        }
        return DeserializationHandlerResponse.FAIL;
    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
