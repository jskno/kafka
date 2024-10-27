package com.linuxacademy.ccdak.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linuxacademy.ccdak.excetion.AppException;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.serialization.Deserializer;

public class JsonDeserializer<T> implements Deserializer<T> {

    private final Class<T> destinationClass;
    private final ObjectMapper objectMapper;

    public JsonDeserializer(Class<T> destinationClass) {
        this(
            new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false),
            destinationClass);
    }

    public JsonDeserializer(ObjectMapper objectMapper, Class<T> destinationClass) {
        this.objectMapper = objectMapper;
        this.destinationClass = destinationClass;
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            return objectMapper.readValue(new String(bytes, StandardCharsets.UTF_8), destinationClass);
        } catch (JsonProcessingException e) {
            throw new AppException("Something went wrong on deserializing message", e);
        }
    }
}
