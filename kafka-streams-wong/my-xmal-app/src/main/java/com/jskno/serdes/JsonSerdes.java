package com.jskno.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class JsonSerdes<T> implements Serde<T> {

    private final JsonSerializer<T> serializer;
    private final JsonDeserializer<T> deserializer;

    private JsonSerdes(Class<T> destinationClass) {
        this.serializer = new JsonSerializer<>();
        this.deserializer = new JsonDeserializer<>(destinationClass);
    }

    public static <T> JsonSerdes<T> of(Class<T> destinationClass) {
        return new JsonSerdes<>(destinationClass);
    }

    @Override
    public Serializer<T> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<T> deserializer() {
        return deserializer;
    }
}
