package com.jskno.topology.ktable;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

@Slf4j
public class ExploreKTableTopologyV4 {

    public static final String WORDS = "words";
    public static final String WORDS_OUTPUT = "words_output";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KTable<String, String> wordsTable = streamsBuilder.table(
            WORDS, Consumed.with(Serdes.String(), Serdes.String()), Materialized.as("words-store"));
        
        wordsTable
            .filter((key, value) -> value.length() > 2)
            .toStream()
            .to(WORDS_OUTPUT, Produced.with(Serdes.String(), Serdes.String()));

        return streamsBuilder.build();
    }

}
