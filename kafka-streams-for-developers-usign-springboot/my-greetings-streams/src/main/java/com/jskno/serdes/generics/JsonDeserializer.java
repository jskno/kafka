package com.jskno.serdes.generics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

@Slf4j
public class JsonDeserializer<T> implements Deserializer<T> {

    private final Class<T> destinationClass;
    private final ObjectMapper objectMapper;

    public JsonDeserializer(Class<T> destinationClass) {
        this.destinationClass = destinationClass;
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }


    public JsonDeserializer(Class<T> destinationClass, ObjectMapper objectMapper) {
        this.destinationClass = destinationClass;
        this.objectMapper = objectMapper;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.readValue(data, destinationClass);
        } catch (IOException e) {
            log.error("IOException: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
