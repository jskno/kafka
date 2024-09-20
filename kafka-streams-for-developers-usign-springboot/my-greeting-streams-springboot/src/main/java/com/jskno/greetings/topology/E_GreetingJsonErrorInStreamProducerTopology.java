package com.jskno.greetings.topology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jskno.greetings.constants.GreetingsConstants;
import com.jskno.greetings.domain.Greeting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class E_GreetingJsonErrorInStreamProducerTopology {

    private final ObjectMapper objectMapper;

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {

        KStream<String, Greeting> greetingsStream = streamsBuilder.stream(
            GreetingsConstants.JSON_GREETINGS,
            Consumed.with(Serdes.String(), new JsonSerde<>(Greeting.class, objectMapper)));

        greetingsStream.print(Printed.<String, Greeting>toSysOut().withLabel("greetingsStream"));

        KStream<String, Greeting> modifiedStream = greetingsStream
            .mapValues((readOnlyKey, value) ->
                new Greeting(value.type(), value.message().toUpperCase(), value.dateTime()));

        modifiedStream.print(Printed.<String, Greeting>toSysOut().withLabel("modifiedStream"));

        modifiedStream.to(
            GreetingsConstants.JSON_GREETINGS_OUTPUT,
            Produced.with(Serdes.String(), new JsonSerde<>(Greeting.class, objectMapper)));
    }


}
