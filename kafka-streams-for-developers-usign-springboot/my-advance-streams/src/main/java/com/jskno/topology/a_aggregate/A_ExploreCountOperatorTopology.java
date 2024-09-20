package com.jskno.topology.a_aggregate;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;

public class A_ExploreCountOperatorTopology {

    public static final String AGGREGATE = "aggregate";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> inputStream = streamsBuilder.stream(
            AGGREGATE, Consumed.with(Serdes.String(), Serdes.String()));

        inputStream.print(Printed.<String, String>toSysOut().withLabel(AGGREGATE));

        KGroupedStream<String, String> groupedByKey = inputStream.groupByKey(
            Grouped.with(Serdes.String(), Serdes.String()));

        // When we used count or any other aggregate operator if we name it we get a topic with that name
        // But we cannot query this topic. To be able to do that we must Materialized the operation
        // Then the previous status can be retrieved if the app is restarted somehow.
        KTable<String, Long> countedByKey = groupedByKey
//            .count(Named.as("count-per-alphabet"));
            .count(Named.as("count-per-alphabet"),
                Materialized.as("count-per-alphabet"));

        countedByKey.toStream().print(Printed.<String, Long>toSysOut().withLabel("words-count-per-alphabet"));

        return streamsBuilder.build();
    }

}
