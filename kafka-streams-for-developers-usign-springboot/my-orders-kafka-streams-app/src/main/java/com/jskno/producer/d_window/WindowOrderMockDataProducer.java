package com.jskno.producer.d_window;

import static java.lang.Thread.sleep;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

@Slf4j
public class WindowOrderMockDataProducer {

    private static KafkaProducer<String, Order> kafkaProducer;

    // Publish Messages Sync
    public static void main(String[] args) throws InterruptedException {

        int count = 0;
        while (count < 10) {
            var orders = buildOrders();
            publishOrders(orders);
            sleep(1000);
            count++;
        }
    }

    private static void publishOrders(List<Order> orders) {
        if (kafkaProducer == null) {
            createKafkaProducer();
        }
        List<ProducerRecord<String, Order>> records = buildOrderRecords(orders);

        records.forEach(record -> {
            try {
                kafkaProducer.send(record).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void createKafkaProducer() {
        Properties producerProperties = createProducerProperties();
        kafkaProducer = new KafkaProducer<>(producerProperties);
    }

    private static Properties createProducerProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }

    private static List<ProducerRecord<String, Order>> buildOrderRecords(List<Order> orders) {
        return orders.stream()
            .map(order -> new ProducerRecord<>(OrderTopology.ORDERS, (String) null, order))
            .toList();
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
            .id(1L)
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
            .id(1L)
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
            .id(1L)
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

        return List.of(order1, order2, order3, order4);
    }

}
