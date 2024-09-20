package com.jskno.serdes.harcoded;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jskno.domain.Greeting;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class GreetingSerdes implements Serde<Greeting> {

    private final ObjectMapper objectMapper;
    private final Serializer<Greeting> serializer;
    private final Deserializer<Greeting> deserializer;

    public GreetingSerdes() {
        this.objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.serializer = new GreetingSerializer(objectMapper);
        this.deserializer = new GreetingDeserializer(objectMapper);
    }

    @Override
    public Serializer<Greeting> serializer() {
        return this.serializer;
    }

    @Override
    public Deserializer<Greeting> deserializer() {
        return this.deserializer;
    }

    public static Serde<Greeting> greetingSerdes() {
        return new GreetingSerdes();
    }

}
