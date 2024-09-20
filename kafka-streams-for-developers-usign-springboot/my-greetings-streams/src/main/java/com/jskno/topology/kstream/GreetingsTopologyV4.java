package com.jskno.topology.kstream;

import com.jskno.domain.Greeting;
import com.jskno.serdes.generics.SerdesFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

@Slf4j
public class GreetingsTopologyV4 {

    public static final String GREETINGS = "greetings";
    public static final String GREETINGS_UPPERCASE = "greetings-uppercase";
    public static final String GREETINGS_SPANISH = "greetings-spanish";

    public static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, Greeting> greetingsStream = streamsBuilder.stream(GREETINGS,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Greeting.class)));
        KStream<String, Greeting> greetingsSpanish = streamsBuilder.stream(GREETINGS_SPANISH,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Greeting.class)));

        var mergedStream = greetingsStream.merge(greetingsSpanish);

        mergedStream
            .print(Printed.<String, Greeting>toSysOut().withLabel("greetingsStream"));
        KStream<String, Greeting> modifiedStream = mergedStream
            .filter((key, value) -> value.message().length() > 5)
            .peek((key, value) -> log.info("after filter --> key: {}, value: {}", key, value))
            .map((key, value) -> KeyValue.pair(
                key.toUpperCase() + "-CC",
                    new Greeting(value.message().toUpperCase() + "-Confluent", value.timeStamp())))
            .peek((key, value) -> log.info("after map --> key: {}, value: {}", key, value))
            .flatMap((key, value) -> {
                List<String> newMessages = Arrays.asList(value.message().split(""));
                return newMessages.stream().map(eachVal -> KeyValue.pair(key, new Greeting(eachVal, value.timeStamp())))
                    .collect(Collectors.toList());
            });
        modifiedStream
            .print(Printed.<String, Greeting>toSysOut().withLabel("modifiedStream"));
        modifiedStream.to(GREETINGS_UPPERCASE,
            Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(Greeting.class)));

        return streamsBuilder.build();
    }
}
