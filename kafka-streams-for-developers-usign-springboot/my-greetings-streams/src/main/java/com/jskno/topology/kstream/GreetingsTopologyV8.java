package com.jskno.topology.kstream;

import com.jskno.domain.Greeting;
import com.jskno.serdes.generics.SerdesFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

@Slf4j
public class GreetingsTopologyV8 {

    public static final String GREETINGS = "greetings";
    public static final String GREETINGS_UPPERCASE = "greetings-uppercase";
    public static final String GREETINGS_SPANISH = "greetings-spanish";

    public static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream<String, Greeting> greetingsStream = streamsBuilder.stream(GREETINGS);
        KStream<String, Greeting> greetingsSpanish = streamsBuilder.stream(GREETINGS_SPANISH);

        var mergedStream = greetingsStream.merge(greetingsSpanish);

        mergedStream
            .print(Printed.<String, Greeting>toSysOut().withLabel("greetingsStream"));

        KStream<String, Greeting> modifiedStream = mergedStream
            .mapValues((readOnlyKey, value) -> {
                if (value.message().equals("TransientError")) {
                    try {
                        throw new IllegalStateException(value.message());
                    } catch (Exception ex) {
                        log.error("Exception in explore errors: {}", ex.getMessage(), ex);
                        return null;
                    }
                }
                    return new Greeting(value.message().toUpperCase(), value.timeStamp());
            })
            .filter((key, value) -> key!= null && value != null);


        modifiedStream
            .print(Printed.<String, Greeting>toSysOut().withLabel("modifiedStream"));
        modifiedStream.to(GREETINGS_UPPERCASE,
            Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(Greeting.class)));

        return streamsBuilder.build();
    }
}
