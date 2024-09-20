package com.jskno.topology.b_join;

import com.jskno.domain.Alphabet;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class B_ExploreKStreamJoinGlobalKTableTopology {

    public static final String ALPHABETS = "alphabets"; // A => First letter in the english alphabet
    public static final String ALPHABETS_ABBREVIATIONS = "alphabets-abbreviations"; // A => Apple

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        joinKStreamWithGlobalKTable(streamsBuilder);

        return streamsBuilder.build();
    }

    private static void joinKStreamWithGlobalKTable(StreamsBuilder streamsBuilder) {

        var alphabetsAbbreviation = streamsBuilder.stream(
            ALPHABETS_ABBREVIATIONS,
            Consumed.with(Serdes.String(), Serdes.String()));

        alphabetsAbbreviation.print(Printed.<String, String>toSysOut().withLabel("alphabets-abbreviation"));

        var alphabetsTable = streamsBuilder.globalTable(
            ALPHABETS,
            Consumed.with(Serdes.String(), Serdes.String()),
            Materialized.as("alphabets-store"));

        KeyValueMapper<String, String, String> keyValueMapper = (leftKey, rightKey) -> leftKey;
        ValueJoiner<String, String, Alphabet> valueJoiner = Alphabet::new;

        var joinedStream = alphabetsAbbreviation.join(alphabetsTable, keyValueMapper, valueJoiner);

        joinedStream.print(Printed.<String, Alphabet>toSysOut().withLabel("alphabets-with-abbreviation"));

    }

}
