package com.github.alexwang.kafka.streams.windowing;

import com.github.alexwang.kafka.streams.model.Patient;
import com.github.alexwang.kafka.streams.model.PatientWithSickRoom;
import com.github.alexwang.kafka.streams.model.SickRoom;
import com.github.alexwang.kafka.streams.serdes.JsonSerdes;
import com.github.alexwang.kafka.streams.statestore.QueryableWindowStateStoreServer;
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

public class PatientHeartBeatMonitorApp {
    private final static Logger LOG = LoggerFactory.getLogger(PatientHeartBeatMonitorApp.class);
    private final static String APP_ID = "patient_heart_beat_monitor_app";
    private final static String BOOTSTRAP_SERVER = "192.168.88.130:9092";
    private final static String HEART_BEAT_TOPIC = "heartbeat";
    private final static String PATIENT_TOPIC = "patient";
    private final static String SICK_ROOM_TOPIC = "sickroom";

    public static void main(String[] args) throws InterruptedException {
        Properties conf = new Properties();
        conf.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        conf.put(StreamsConfig.STATE_DIR_CONFIG, "C:\\WorkBench\\Work\\kafka\\statestore");
        conf.put(StreamsConfig.APPLICATION_ID_CONFIG, APP_ID);
        conf.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 3);
        conf.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 0);
        conf.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Long> heartbeatKStream = builder.stream(HEART_BEAT_TOPIC, Consumed.with(Serdes.String(), Serdes.String()).withName("heartbeat-sensor-source-processor")
                        .withOffsetResetPolicy(Topology.AutoOffsetReset.LATEST))
                .groupBy((nokey, patient) -> patient, Grouped.with(Serdes.String(), Serdes.String()))
                .windowedBy(TimeWindows.ofSizeAndGrace(Duration.ofMinutes(1), Duration.ofSeconds(1)))
                .count(Named.as("heartbeat-count"), Materialized.as("heartbeat-state-store"))
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .peek((k, v) -> LOG.info("pre filtering,{}-{}", k, v))
                .filter((k, v) -> v > 80)
                .peek((k, v) -> LOG.info("post filtering,{}-{}", k, v))
                .selectKey((k, v) -> k.key());

        KTable<String, Patient> patientKTable = builder.table(PATIENT_TOPIC, Consumed.with(Serdes.String(), JsonSerdes.PatientSerde())
                .withName("patient-source").withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));

        KTable<String, SickRoom> sickRoomKTable = builder.table(SICK_ROOM_TOPIC, Consumed.with(Serdes.String(), JsonSerdes.SickRoomSerde())
                .withName("sickroom-source").withOffsetResetPolicy(Topology.AutoOffsetReset.EARLIEST));

        KTable<String, PatientWithSickRoom> patientWithSickRoomKTable = patientKTable.join(sickRoomKTable, Patient::getSickRoomID, PatientWithSickRoom::new,
                Materialized.<String, PatientWithSickRoom, KeyValueStore<Bytes, byte[]>>as("patient-sickroom")
                        .withValueSerde(JsonSerdes.PatientWithSickRoomSerde()));

        heartbeatKStream.join(patientWithSickRoomKTable, (h, p) -> {
                    p.setHeartBeat(h);
                    return p;
                }, Joined.with(Serdes.String(), Serdes.Long(), JsonSerdes.PatientWithSickRoomSerde()))
                .peek((k, v) -> LOG.info("joined: {}---{}", k, v))
                .filter((k, v) -> {
                    if (v.getHeartBeat() < 100) {
                        return v.getPatient().getPatientAge() > 25;
                    }
                    return true;
                })
                .print(Printed.<String, PatientWithSickRoom>toSysOut().withLabel("hc-monitor-warning"));


        Topology topology = builder.build();
        KafkaStreams kafkaStreams = new KafkaStreams(topology, conf);
        new QueryableWindowStateStoreServer(kafkaStreams, "heartbeat-state-store").start();
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kafkaStreams.close();
            latch.countDown();
            LOG.info("The kafka streams application is closed.");
        }));
        kafkaStreams.start();
        LOG.info("The kafka streams application start...");
        latch.await();
    }
}
