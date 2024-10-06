package com.jskno.producer.e_my_exercise;

import com.jskno.constants.OrdersConstants;
import com.jskno.domain.booking.GwyBooking;
import com.jskno.domain.order.Order;
import com.jskno.domain.order.OrderLineItem;
import com.jskno.domain.order.OrderType;
import com.jskno.serdes.JsonSerializer;
import com.jskno.topology.a_kstream_split.OrderTopology;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class B_GwyBookingProducerJoin {

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();
        try (KafkaProducer<String, GwyBooking> kafkaProducer = new KafkaProducer<>(properties)) {
            List<ProducerRecord<String, GwyBooking>> records = buildOrderRecords();

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
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return properties;
    }

    private static List<GwyBooking> buildOrders() {
        GwyBooking booking1 = GwyBooking.builder()
            .id("a")
            .branch("ALC")
            .category("Luxury")
            .build();

        GwyBooking booking2 = GwyBooking.builder()
            .id("b")
            .branch("PAR")
            .category("Luxury")
            .build();

        GwyBooking booking3 = GwyBooking.builder()
            .id("c")
            .branch("ALC")
            .category("Basic")
            .build();

        return List.of(booking1, booking2, booking3);
    }

    private static List<ProducerRecord<String, GwyBooking>> buildOrderRecords() {
        List<GwyBooking> bookings = buildOrders();
        return bookings.stream()
            .map(booking -> new ProducerRecord<>(OrdersConstants.GWY_BOOKINGS, booking.id(), booking))
            .toList();
    }

}
