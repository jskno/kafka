package com.linuxacademy.ccdak.kafkaSimpleConsumer.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonDeserializer<T> implements Deserializer<T> {

    private final static Logger log = LoggerFactory.getLogger(JsonDeserializer.class);

    private final ObjectMapper objectMapper;
    private final Class<T> destinationClass;

    public JsonDeserializer(Class<T> destinationClass) {
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.destinationClass = destinationClass;
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            return objectMapper.readValue(new String(bytes, StandardCharsets.UTF_8), destinationClass);
        }catch (JsonProcessingException e) {
            log.error("JsonProcessingException Deserializing to {} : {} ", destinationClass, e.getMessage(), e);
            throw new RuntimeException(e);
        }catch (Exception e){
            log.error("Exception Deserializing to {} : {} ", destinationClass, e.getMessage(), e);
            throw e;
        }
    }
}
