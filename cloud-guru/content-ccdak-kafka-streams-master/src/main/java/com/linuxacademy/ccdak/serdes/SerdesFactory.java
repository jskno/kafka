package com.linuxacademy.ccdak.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class SerdesFactory {

    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static <T> Serde<T> jsonSerdes(Class<T> destinaltionClass) {
        return Serdes.serdeFrom(
            new JsonSerializer<>(objectMapper),
            new JsonDeserializer<>(objectMapper, destinaltionClass));
    }

}
