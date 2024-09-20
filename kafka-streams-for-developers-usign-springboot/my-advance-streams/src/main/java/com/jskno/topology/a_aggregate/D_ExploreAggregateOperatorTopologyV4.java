package com.jskno.topology.a_aggregate;

import com.jskno.domain.AlphabetWordAggregate;
import com.jskno.serdes.SerdesFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueStore;

@Slf4j
public class D_ExploreAggregateOperatorTopologyV4 {

    public static final String AGGREGATE = "aggregate-v4";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> inputStream = streamsBuilder.stream(
            AGGREGATE, Consumed.with(Serdes.String(), Serdes.String()));

        inputStream.print(Printed.<String, String>toSysOut().withLabel(AGGREGATE));

        KGroupedStream<String, String> groupedByKey = inputStream
            .groupByKey(Grouped.with(Serdes.String(), Serdes.String()));

        Initializer<AlphabetWordAggregate> initializer = AlphabetWordAggregate::new;
        Aggregator<String, String, AlphabetWordAggregate> aggregator = (key, value, alphabetWordAggregate) ->
            alphabetWordAggregate.updateNewEvents(key, value);

        KTable<String, AlphabetWordAggregate> aggregatedStream = groupedByKey
            .aggregate(
                initializer,
                aggregator,
                Materialized.<String, AlphabetWordAggregate, KeyValueStore<Bytes, byte[]>>as("aggregated-store")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(SerdesFactory.jsonSerdes(AlphabetWordAggregate.class)));

        aggregatedStream.toStream().print(Printed.<String, AlphabetWordAggregate>toSysOut().withLabel("aggregate-words-per-alphabet"));

        return streamsBuilder.build();
    }

}
