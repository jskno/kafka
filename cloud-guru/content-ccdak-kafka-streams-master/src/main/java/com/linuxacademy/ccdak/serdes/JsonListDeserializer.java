package com.linuxacademy.ccdak.serdes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.linuxacademy.ccdak.excetion.AppException;
import com.linuxacademy.ccdak.model.Inventory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.lang.model.type.ReferenceType;
import org.apache.kafka.common.serialization.Deserializer;

public class JsonListDeserializer<T> implements Deserializer<T> {

    private final Class<T> destinationClass;
    private final ObjectMapper objectMapper;

    public JsonListDeserializer(Class<T> destinationClass) {
        this(
            new ObjectMapper().registerModule(new JavaTimeModule()).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false),
            destinationClass);
    }

    public JsonListDeserializer(ObjectMapper objectMapper, Class<T> destinationClass) {
        this.objectMapper = objectMapper;
        this.destinationClass = destinationClass;
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            List<Inventory> myObjects = objectMapper.readValue(bytes, new TypeReference<List<Inventory>>(){});
            Class<? extends Class> aClass = destinationClass.getClass();
//            List<aClass> o = objectMapper.readValue(bytes,
//                objectMapper.getTypeFactory().constructCollectionType(List.class, destinationClass));
//            new TypeReference<List<detinationClass>>(){}
            return objectMapper.readValue(new String(bytes, StandardCharsets.UTF_8), destinationClass);
        } catch (JsonProcessingException e) {
            throw new AppException("Something went wrong on deserializing message", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
