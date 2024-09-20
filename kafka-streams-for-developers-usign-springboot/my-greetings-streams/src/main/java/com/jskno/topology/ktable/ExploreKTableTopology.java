package com.jskno.topology.ktable;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;

@Slf4j
public class ExploreKTableTopology {

    public static final String WORDS = "words";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KTable<String, String> wordsTable = streamsBuilder.table(
            WORDS,
            Consumed.with(Serdes.String(), Serdes.String()),
            Materialized.as("words-store"));

        wordsTable
            .filter((key, value) -> value.length() > 2)
            .toStream()
            .print(Printed.<String, String>toSysOut().withLabel("workds-ktable"));

        return streamsBuilder.build();
    }

}
