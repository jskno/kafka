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
public class B_ExploreCountTumblingSuppressedWindowTopology {

    public static String WINDOW_WORDS = "window-words";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, String> wordsStream =
            streamsBuilder.stream(WINDOW_WORDS, Consumed.with(Serdes.String(), Serdes.String()));

        tumblingWindows(wordsStream);

        return streamsBuilder.build();
    }

    private static void tumblingWindows(KStream<String, String> wordsStream) {

        Duration windowSize = Duration.ofSeconds(5);
        TimeWindows timeWindow = TimeWindows.ofSizeWithNoGrace(windowSize);

        KTable<Windowed<String>, Long> windowedTable = wordsStream
            .groupByKey()
            .windowedBy(timeWindow)
            .count()
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull()));

        windowedTable.toStream()
            .peek(B_ExploreCountTumblingSuppressedWindowTopology::printLocalDateTimes)
            .print(Printed.<Windowed<String>, Long>toSysOut());
    }

    private static void printLocalDateTimes(Windowed<String> key, Long value) {
        var startTime = key.window().startTime();
        var endTime = key.window().endTime();
        log.info("startTime: {}, endTime: {}, Count: {}", startTime, endTime, value);

        LocalDateTime startLDT = LocalDateTime.ofInstant(startTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        LocalDateTime endLDT = LocalDateTime.ofInstant(endTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        log.info("TumblingWindows: key {}, value: {}", key, value);
        log.info("startLDT: {}, endLDT: {}, Count: {}", startLDT, endLDT, value);
    }

}
