package com.jskno.g_state_store;

import com.jskno.f_complex_streams.model.Patient;
import com.jskno.f_complex_streams.model.PatientWithSickRoom;
import com.jskno.f_complex_streams.model.SickRoom;
import com.jskno.serdes.JsonSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


// sudo ./bin/kafka-server-start.sh config/kraft/server.properties
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic heartbeat --partitions 3 --replication-factor 1
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic patient --partitions 3 --replication-factor 1
// ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic sickroom --partitions 3 --replication-factor 1

// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic patient --property parse.key=true --property key.separator=,
// ./bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic sickroom --property parse.key=true --property key.separator=,
public class A1_HearBeatMonitoringApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(A1_HearBeatMonitoringApp.class);

    public static void main(String[] args) throws InterruptedException {
        Properties props = buildStreamsProperties();
        Topology topology = buildTopology();

        KafkaStreams streams = new KafkaStreams(topology, props);
        new A0_QueryableWindowStateStoreServer(streams, "heartbeat-state-store").start();

        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            streams.close();
            latch.countDown();
            LOGGER.info("The WordProcessorApp is gracefully shutting down");
        }));

        streams.start();
        LOGGER.info("WordProcessorApp is started");

        latch.await();
    }

    private static Properties buildStreamsProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "heartbeat-monitoring");
        props.put(StreamsConfig.STATE_DIR_CONFIG, "/home/jskno/kafka-logs/statestore");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        return props;
    }

    private static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, Long> heartbeatKStream = builder.stream(
                        "heartbeat",
                        Consumed.with(Serdes.String(), Serdes.String())
                                .withName("heartbeat-processor")
                                .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .groupBy((nokey, patient) -> patient, Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(1)))
                .count(Named.as("heartbeat-count"), Materialized.as("heartbeat-state-store"))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .peek((k, v) -> LOGGER.info("Pre Filtering, {}-{}", k, v))
                .filter((patient, count) -> count > 80)
                .peek((k, v) -> LOGGER.info("Post Filtering, {}-{}", k, v))
                .selectKey((k, v) -> k.key());

        KTable<String, Patient> patientKTable = builder.table(
                "patient",
                Consumed.with(Serdes.String(), JsonSerdes.of(Patient.class))
                        .withName("patient-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));

        KTable<String, SickRoom> sickroomKTable = builder.table(
                "sickroom",
                Consumed.with(Serdes.String(), JsonSerdes.of(SickRoom.class))
                        .withName("sickroom-source")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));

        KTable<String, PatientWithSickRoom> patientWithSickRoomKTable = patientKTable.join(sickroomKTable, Patient::getSickRoomId, PatientWithSickRoom::new,
                Materialized.<String, PatientWithSickRoom, KeyValueStore<Bytes, byte[]>>as("patient-sickroom")
                        .withValueSerde(JsonSerdes.of(PatientWithSickRoom.class)));

        heartbeatKStream.join(patientWithSickRoomKTable, (h, p) -> {
                    p.setHeartBeat(h);
                    return p;
                }, Joined.with(Serdes.String(), Serdes.Long(), JsonSerdes.of(PatientWithSickRoom.class)))
                .peek((k, v) -> LOGGER.info("Joined, {}---{}", k, v))
                .filter((k, v) -> {
                    if (v.getHeartBeat() < 100) {
                        return v.getPatient().getAge() > 25;
                    }
                    return true;
                })
                .print(Printed.<String, PatientWithSickRoom>toSysOut().withLabel("health-check-warning"));

        return builder.build();
    }
}
