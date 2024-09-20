package com.jskno.topology.b_join;

import com.jskno.domain.Alphabet;
import java.time.Duration;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class D_ExploreKStreamJoinKStreamTopology {

    public static final String ALPHABETS = "alphabets"; // A => First letter in the english alphabet
    public static final String ALPHABETS_ABBREVIATIONS = "alphabets-abbreviations"; // A => Apple

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        joinKStreamWithKStream(streamsBuilder);

        return streamsBuilder.build();
    }

    private static void joinKStreamWithKStream(StreamsBuilder streamsBuilder) {

        var alphabetsAbbreviationStream = streamsBuilder.stream(
            ALPHABETS_ABBREVIATIONS,
            Consumed.with(Serdes.String(), Serdes.String()));

        alphabetsAbbreviationStream.print(Printed.<String, String>toSysOut().withLabel("alphabets-abbreviations"));

        var alphabetsStream = streamsBuilder.stream(
            ALPHABETS,
            Consumed.with(Serdes.String(), Serdes.String()));

        alphabetsStream.print(Printed.<String, String>toSysOut().withLabel("alphabets"));

        ValueJoiner<String, String, Alphabet> valueJoiner = Alphabet::new;
        JoinWindows joinWindows = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(5));
        StreamJoined<String, String, String> joinedParams = StreamJoined.with(
            Serdes.String(), Serdes.String(), Serdes.String());


        var joinedStream = alphabetsAbbreviationStream.join(alphabetsStream, valueJoiner, joinWindows, joinedParams);

        joinedStream.print(Printed.<String, Alphabet>toSysOut().withLabel("alphabets-with-abbreviation"));

    }

}
