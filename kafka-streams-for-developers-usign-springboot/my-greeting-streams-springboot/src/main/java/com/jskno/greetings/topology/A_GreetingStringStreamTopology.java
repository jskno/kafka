package com.jskno.greetings.topology;

import com.jskno.greetings.constants.GreetingsConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
@Slf4j
public class A_GreetingStringStreamTopology {

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {

        KStream<String, String> greetingsStream = streamsBuilder.stream(
            GreetingsConstants.GREETINGS,
            Consumed.with(Serdes.String(), Serdes.String()));

        greetingsStream.print(Printed.<String, String>toSysOut().withLabel("greetingsStream"));

        KStream<String, String> modifiedStream = greetingsStream
            .mapValues((readOnlyKey, value) -> value.toUpperCase());

        modifiedStream.print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));

        modifiedStream.to(
            GreetingsConstants.GREETINGS_OUTPUT,
            Produced.with(Serdes.String(), Serdes.String()));
    }


}
