package com.jskno.topology.b_join;

import com.jskno.domain.Alphabet;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class C_ExploreKTableJoinKTableTopology {

    public static final String ALPHABETS = "alphabets"; // A => First letter in the english alphabet
    public static final String ALPHABETS_ABBREVIATIONS = "alphabets-abbreviations"; // A => Apple

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        joinKTableWithKTable(streamsBuilder);

        return streamsBuilder.build();
    }

    private static void joinKTableWithKTable(StreamsBuilder streamsBuilder) {

        var alphabetsAbbreviationTable = streamsBuilder.table(
            ALPHABETS_ABBREVIATIONS,
            Consumed.with(Serdes.String(), Serdes.String()),
            Materialized.as("alphabets-abbreviation-store"));

        alphabetsAbbreviationTable.toStream().print(Printed.<String, String>toSysOut().withLabel("alphabets-abbreviations"));

        var alphabetsTable = streamsBuilder.table(
            ALPHABETS,
            Consumed.with(Serdes.String(), Serdes.String()),
            Materialized.as("alphabets-store"));

        alphabetsTable.toStream().print(Printed.<String, String>toSysOut().withLabel("alphabets"));

        ValueJoiner<String, String, Alphabet> valueJoiner = Alphabet::new;

        var joinedStream = alphabetsAbbreviationTable.join(alphabetsTable, valueJoiner);

        joinedStream.toStream().print(Printed.<String, Alphabet>toSysOut().withLabel("alphabets-with-abbreviation"));

    }

}
