package com.jskno.topology.c_window;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;

@Slf4j
public class C_ExploreHopingWindowTopology {

    public static String WINDOW_WORDS = "window-words";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> wordsStream =
            streamsBuilder.stream(WINDOW_WORDS, Consumed.with(Serdes.String(), Serdes.String()));

        hopingWindows(wordsStream);

        return streamsBuilder.build();
    }

    private static void hopingWindows(KStream<String, String> wordsStream) {

        Duration windowSize = Duration.ofSeconds(5);
        Duration advancedBySize = Duration.ofSeconds(3);

        TimeWindows hopingWindow = TimeWindows
            .ofSizeWithNoGrace(windowSize)
            .advanceBy(advancedBySize);

        KTable<Windowed<String>, Long> windowedTable = wordsStream
            .groupByKey()
            .windowedBy(hopingWindow)
            .count()
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull()));

        windowedTable.toStream()
            .peek(C_ExploreHopingWindowTopology::printLocalDateTimes)
            .print(Printed.<Windowed<String>, Long>toSysOut());
    }

    private static void printLocalDateTimes(Windowed<String> key, Long value) {
        var startTime = key.window().startTime();
        var endTime = key.window().endTime();
        log.info("startTime: {}, endTime: {}, Count: {}", startTime, endTime, value);

        LocalDateTime startLDT = LocalDateTime.ofInstant(startTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        LocalDateTime endLDT = LocalDateTime.ofInstant(endTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        log.info("HopingWindows: key {}, value: {}", key, value);
        log.info("startLDT: {}, endLDT: {}, Count: {}", startLDT, endLDT, value);
    }

}
