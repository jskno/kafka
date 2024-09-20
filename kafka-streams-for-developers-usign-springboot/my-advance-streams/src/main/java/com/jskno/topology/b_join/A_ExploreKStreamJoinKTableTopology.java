package com.jskno.topology.b_join;

import com.jskno.domain.Alphabet;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class A_ExploreKStreamJoinKTableTopology {

    public static final String ALPHABETS = "alphabets"; // A => First letter in the english alphabet
    public static final String ALPHABETS_ABBREVIATIONS = "alphabets-abbreviations"; // A => Apple

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        joinKStreamWithTable(streamsBuilder);

        return streamsBuilder.build();
    }

    private static void joinKStreamWithTable(StreamsBuilder streamsBuilder) {

        KStream<String, String> alphabetAbbreviation = streamsBuilder.stream(
            ALPHABETS_ABBREVIATIONS, Consumed.with(Serdes.String(), Serdes.String()));

        alphabetAbbreviation.print(Printed.<String, String>toSysOut().withLabel(ALPHABETS_ABBREVIATIONS));

        KTable<String, String> alphabetsTable = streamsBuilder.table(
            ALPHABETS,
            Consumed.with(Serdes.String(), Serdes.String()),
            Materialized.as("alphabets-store"));

        alphabetsTable.toStream().print(Printed.<String, String>toSysOut().withLabel(ALPHABETS));

        ValueJoiner<String, String, Alphabet> valueJoiner = Alphabet::new;

        //[alphabets-with-abbreviations]: A, Alphabet[abbreviation=Apple, description=A is the first letter in English Alphabets.]
        //[alphabets-with-abbreviations]: B, Alphabet[abbreviation=Bus, description=B is the second letter in English Alphabets.]
        KStream<String, Alphabet> joinedStream = alphabetAbbreviation.join(alphabetsTable, valueJoiner);

        joinedStream.print(Printed.<String, Alphabet>toSysOut().withLabel("alphabets-with-abbreviation"));

    }

}
