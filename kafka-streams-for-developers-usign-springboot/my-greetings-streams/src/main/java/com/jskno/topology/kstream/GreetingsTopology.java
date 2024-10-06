package com.jskno.topology.kstream;

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
public class GreetingsTopology {

    public static final String GREETINGS = "greetings";
    public static final String GREETINGS_UPPERCASE = "greetings-uppercase";
    public static final String GREETINGS_SPANISH = "greetings-spanish";

    public static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> greetingsStream = streamsBuilder
            .stream(GREETINGS, Consumed.with(Serdes.String(), Serdes.String()));
        KStream<String, String> greetingsSpanish = streamsBuilder
            .stream(GREETINGS_SPANISH, Consumed.with(Serdes.String(), Serdes.String()));

        KStream<String, String> mergedStream = greetingsStream.merge(greetingsSpanish);

        mergedStream
            .print(Printed.<String, String>toSysOut().withLabel("greetingsStream"));
        KStream<String, String> modifiedStream = mergedStream
            .filter((key, value) -> value.length() > 5)
            .peek((key, value) -> log.info("after filter --> key: {}, value: {}", key, value))
            .map((key, value) -> KeyValue.pair(key.toUpperCase() + "-CC", value.toUpperCase() + "-ConfluentCertification"))
            .peek((key, value) -> log.info("after map --> key: {}, value: {}", key, value))
            .mapValues((readOnlyKey, value) -> value.toUpperCase())
            .flatMap((key, value) -> {
                List<String> newValues = Arrays.asList(value.split(""));
                return newValues.stream().map(eachVal -> KeyValue.pair(key, eachVal)).collect(Collectors.toList());
            });
        modifiedStream
            .print(Printed.<String, String>toSysOut().withLabel("modifiedStream"));
        modifiedStream
            .to(GREETINGS_UPPERCASE, Produced.with(Serdes.String(), Serdes.String()));

        return streamsBuilder.build();
    }
}
