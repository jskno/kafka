package com.jskno.topology.a_aggregate;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;

@Slf4j
public class C_ExploreReduceOperatorTopologyV3 {

    public static final String AGGREGATE = "aggregate-v3";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> inputStream = streamsBuilder.stream(AGGREGATE, Consumed.with(Serdes.String(), Serdes.String()));

        inputStream.print(Printed.<String, String>toSysOut().withLabel(AGGREGATE));

        KGroupedStream<String, String> groupedByKey = inputStream
            .groupByKey(Grouped.with(Serdes.String(), Serdes.String()));

        KTable<String, String> reducedStream = groupedByKey.reduce((value1, value2) -> {
            log.info("Value1: {}, value2: {}", value1, value2);
            return value1.toUpperCase() + "-" + value2.toUpperCase();
        },
            Materialized.<String, String, KeyValueStore< Bytes, byte[]>>as("reduce-words")
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.String()));

        reducedStream.toStream().print(Printed.<String, String>toSysOut().withLabel("reduce-words-per-alphabet"));

        return streamsBuilder.build();
    }

}
