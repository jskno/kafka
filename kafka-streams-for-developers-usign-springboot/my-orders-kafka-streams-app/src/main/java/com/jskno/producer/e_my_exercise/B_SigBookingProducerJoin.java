package com.jskno.producer.e_my_exercise;

import com.jskno.constants.OrdersConstants;
import com.jskno.domain.booking.GwyBooking;
import com.jskno.domain.booking.SigBooking;
import com.jskno.serdes.JsonSerializer;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class B_SigBookingProducerJoin {

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();
        try (KafkaProducer<Long, SigBooking> kafkaProducer = new KafkaProducer<>(properties)) {
            List<ProducerRecord<Long, SigBooking>> records = buildOrderRecords();

            records.forEach(record -> {
                try {
                    kafkaProducer.send(record).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static Properties buildProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return properties;
    }

    private static List<SigBooking> buildOrders() {
        SigBooking booking1 = SigBooking.builder()
            .bookingId(1L)
            .station("ALC")
            .car("Seat")
            .build();

        SigBooking booking2 = SigBooking.builder()
            .bookingId(2L)
            .station("PAR")
            .car("Mercedes")
            .build();

        SigBooking booking3 = SigBooking.builder()
            .bookingId(3L)
            .station("ALC")
            .car("Audi")
            .build();

        return List.of(booking1, booking2, booking3);
    }

    private static List<ProducerRecord<Long, SigBooking>> buildOrderRecords() {
        List<SigBooking> bookings = buildOrders();
        return bookings.stream()
            .map(booking -> new ProducerRecord<>(OrdersConstants.SIG_BOOKINGS, booking.bookingId(), booking))
            .toList();
    }

}
