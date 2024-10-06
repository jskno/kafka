package com.jskno.producer.e_my_exercise;

import com.jskno.constants.OrdersConstants;
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

public class A_OrderProducerJoin {

    public static void main(String[] args) {
        Properties properties = buildProducerProperties();
        try (KafkaProducer<String, Order> kafkaProducer = new KafkaProducer<>(properties)) {
            List<ProducerRecord<String, Order>> records = buildOrderRecords();

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

    private static List<Order> buildOrders() {
        Order order1 = Order.builder()
            .id(1L)
            .locationId("location1")
            .orderType(OrderType.GENERAL)
            .orderDateTime(OffsetDateTime.now())
            .finalAmount(BigDecimal.valueOf(105.78))
            .lineItems(List.of(
                OrderLineItem.builder().item("Item11").count(10).amount(BigDecimal.valueOf(5.75)).build(),
                OrderLineItem.builder().item("Item12").count(15).amount(BigDecimal.valueOf(3.25)).build(),
                OrderLineItem.builder().item("Item13").count(3).amount(BigDecimal.valueOf(255.08)).build()
            ))
            .build();

        Order order2 = Order.builder()
            .id(2L)
            .locationId("location1")
            .orderType(OrderType.RESTAURANT)
            .orderDateTime(OffsetDateTime.now())
            .finalAmount(BigDecimal.valueOf(105.78))
            .lineItems(List.of(
                OrderLineItem.builder().item("Item21").count(10).amount(BigDecimal.valueOf(5.75)).build(),
                OrderLineItem.builder().item("Item22").count(15).amount(BigDecimal.valueOf(3.25)).build(),
                OrderLineItem.builder().item("Item23").count(3).amount(BigDecimal.valueOf(255.08)).build()
            ))
            .build();

        Order order3 = Order.builder()
            .id(3L)
            .locationId("location2")
            .orderType(OrderType.GENERAL)
            .orderDateTime(OffsetDateTime.now())
            .finalAmount(BigDecimal.valueOf(105.78))
            .lineItems(List.of(
                OrderLineItem.builder().item("Item31").count(10).amount(BigDecimal.valueOf(5.75)).build(),
                OrderLineItem.builder().item("Item32").count(15).amount(BigDecimal.valueOf(3.25)).build(),
                OrderLineItem.builder().item("Item33").count(3).amount(BigDecimal.valueOf(255.08)).build()
            ))
            .build();

        Order order4 = Order.builder()
            .id(4L)
            .locationId("location2")
            .orderType(OrderType.RESTAURANT)
            .orderDateTime(OffsetDateTime.now())
            .finalAmount(BigDecimal.valueOf(105.78))
            .lineItems(List.of(
                OrderLineItem.builder().item("Item41").count(10).amount(BigDecimal.valueOf(5.75)).build(),
                OrderLineItem.builder().item("Item42").count(15).amount(BigDecimal.valueOf(3.25)).build(),
                OrderLineItem.builder().item("Item43").count(3).amount(BigDecimal.valueOf(255.08)).build()
            ))
            .build();

        // No store for this order in StoreTable
        Order order5 = Order.builder()
            .id(5L)
            .locationId("location999")
            .orderType(OrderType.RESTAURANT)
            .orderDateTime(OffsetDateTime.now())
            .finalAmount(BigDecimal.valueOf(55.55))
            .lineItems(List.of(
                OrderLineItem.builder().item("Item41").count(10).amount(BigDecimal.valueOf(25.75)).build(),
                OrderLineItem.builder().item("Item42").count(15).amount(BigDecimal.valueOf(23.25)).build(),
                OrderLineItem.builder().item("Item43").count(3).amount(BigDecimal.valueOf(25.08)).build()
            ))
            .build();

        return List.of(order1, order2, order3, order4, order5);
    }

    private static List<ProducerRecord<String, Order>> buildOrderRecords() {
        List<Order> orders = buildOrders();
        return orders.stream()
            .map(order -> new ProducerRecord<>(OrdersConstants.ORDERS_TOPIC, (String) null, order))
            .toList();
    }

}
