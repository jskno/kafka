package com.jskno.topology.a_aggregate;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;

public class B_ExploreCountOperatorTopologyV2 {

    public static final String AGGREGATE = "aggregate-v2";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> inputStream = streamsBuilder.stream(
            AGGREGATE, Consumed.with(Serdes.String(), Serdes.String()));

        inputStream.print(Printed.<String, String>toSysOut().withLabel(AGGREGATE));

        KGroupedStream<String, String> groupedByKey = inputStream
            .groupBy((key, value) -> value, Grouped.with(Serdes.String(), Serdes.String()));

        KTable<String, Long> countedByKey = groupedByKey.count(Named.as("count-per-word"));

        countedByKey.toStream().print(Printed.<String, Long>toSysOut().withLabel("words-count-per-word"));

        return streamsBuilder.build();
    }

}
