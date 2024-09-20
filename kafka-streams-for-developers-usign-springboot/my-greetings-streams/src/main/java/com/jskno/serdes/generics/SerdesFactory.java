package com.jskno.serdes.generics;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public class SerdesFactory {

    public static <T> Serde<T> jsonSerdes(Class<T> destinationClass) {
        JsonSerializer<T> jsonSerializer = new JsonSerializer<>();
        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(destinationClass);
        return Serdes.serdeFrom(jsonSerializer, jsonDeserializer);
    }

}
