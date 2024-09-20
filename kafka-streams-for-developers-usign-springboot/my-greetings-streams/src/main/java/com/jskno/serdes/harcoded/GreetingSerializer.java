package com.jskno.serdes.harcoded;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jskno.domain.Greeting;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

@Slf4j
public class GreetingSerializer implements Serializer<Greeting> {

    private final ObjectMapper objectMapper;

    public GreetingSerializer() {
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public GreetingSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(String topic, Greeting greeting) {
        try {
            return objectMapper.writeValueAsBytes(greeting);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
