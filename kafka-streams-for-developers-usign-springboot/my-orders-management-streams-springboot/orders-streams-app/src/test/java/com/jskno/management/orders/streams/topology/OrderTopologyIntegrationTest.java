package com.jskno.management.orders.streams.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jskno.management.orders.domain.dto.OrdersRevenuePerStoreByWindowsDTO;
import com.jskno.management.orders.domain.order.Order;
import com.jskno.management.orders.domain.order.OrderLineItem;
import com.jskno.management.orders.domain.order.OrderType;
import com.jskno.management.orders.streams.constants.OrdersConstants;
import com.jskno.management.orders.streams.service.OrderService;
import com.jskno.management.orders.streams.service.OrderWindowService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import org.apache.kafka.streams.KeyValue;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(topics = {OrdersConstants.ORDERS, OrdersConstants.STORES})
@TestPropertySource(properties = {
    "spring.kafka.streams.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
    "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
    "spring.kafka.streams.auto-startup=false"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderTopologyIntegrationTest {

    @Autowired
    KafkaTemplate<String, Order> kafkaTemplate;

    @Autowired
    StreamsBuilderFactoryBean streamsBuilderFactoryBean;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderWindowService orderWindowService;

    @BeforeEach
    public void setUp() {
        streamsBuilderFactoryBean.start();
    }

    @AfterEach
    public void destroy() {
        Objects.requireNonNull(streamsBuilderFactoryBean.getKafkaStreams()).close();
        streamsBuilderFactoryBean.getKafkaStreams().cleanUp();
    }

    @Test
    void ordersCountTest() {
        // Given
        publishOrders();

        // When

        // Then
        Awaitility.await().atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .ignoreExceptions()
            .until(() -> orderService.getOrdersCountByType(OrdersConstants.GENERAL_ORDERS).size(), Matchers.equalTo(1));

        var generalOrdersCount = orderService.getOrdersCountByType(OrdersConstants.GENERAL_ORDERS);
        assertEquals(1, generalOrdersCount.getFirst().orderCount());
    }

    @Test
    void ordersRevenueTest() {
        // Given
        publishOrders();

        // When

        // Then
        Awaitility.await().atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .ignoreExceptions()
            .until(() -> orderService.getOrdersRevenueByType(OrdersConstants.GENERAL_ORDERS).size(), Matchers.equalTo(1));

        Awaitility.await().atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .ignoreExceptions()
            .until(() -> orderService.getOrdersRevenueByType(OrdersConstants.RESTAURANT_ORDERS).size(), Matchers.equalTo(1));

        var generalOrdersRevenue = orderService.getOrdersRevenueByType(OrdersConstants.GENERAL_ORDERS);
        assertEquals(1, generalOrdersRevenue.getFirst().totalRevenue().runningOrderCount());
        assertEquals(new BigDecimal("27.00"), generalOrdersRevenue.getFirst().totalRevenue().runningRevenue());

        var restaurantOrdersRevenue = orderService.getOrdersRevenueByType(OrdersConstants.RESTAURANT_ORDERS);
        assertEquals(1, restaurantOrdersRevenue.getFirst().totalRevenue().runningOrderCount());
        assertEquals(new BigDecimal("15.00"), restaurantOrdersRevenue.getFirst().totalRevenue().runningRevenue());
    }

    @Test
    void ordersRevenue_WhenMultipleOrdersTest() {
        // Given
        publishOrders();
        publishOrders();

        // When

        // Then
        Awaitility.await().atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .ignoreExceptions()
            .until(() -> orderService.getOrdersRevenueByType(OrdersConstants.GENERAL_ORDERS).size(), Matchers.equalTo(1));

        Awaitility.await().atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .ignoreExceptions()
            .until(() -> orderService.getOrdersRevenueByType(OrdersConstants.RESTAURANT_ORDERS).size(), Matchers.equalTo(1));

        var generalOrdersRevenue = orderService.getOrdersRevenueByType(OrdersConstants.GENERAL_ORDERS);
        assertEquals(2, generalOrdersRevenue.getFirst().totalRevenue().runningOrderCount());
        assertEquals(new BigDecimal("54.00"), generalOrdersRevenue.getFirst().totalRevenue().runningRevenue());

        var restaurantOrdersRevenue = orderService.getOrdersRevenueByType(OrdersConstants.RESTAURANT_ORDERS);
        assertEquals(2, restaurantOrdersRevenue.getFirst().totalRevenue().runningOrderCount());
        assertEquals(new BigDecimal("30.00"), restaurantOrdersRevenue.getFirst().totalRevenue().runningRevenue());
    }

    @Test
    void ordersRevenueByWindows_WhenMultipleOrdersTest() {
        // Given
        publishOrders();
        publishOrders();

        // When

        // Then
        Awaitility.await().atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .ignoreExceptions()
            .until(() -> orderWindowService.getOrdersRevenueWindowByType(OrdersConstants.GENERAL_ORDERS).size(), Matchers.equalTo(1));

        Awaitility.await().atMost(Duration.ofSeconds(10))
            .pollDelay(Duration.ofSeconds(1))
            .ignoreExceptions()
            .until(() -> orderWindowService.getOrdersRevenueWindowByType(OrdersConstants.RESTAURANT_ORDERS).size(), Matchers.equalTo(1));

        List<OrdersRevenuePerStoreByWindowsDTO> generalOrdersRevenue = orderWindowService.getOrdersRevenueWindowByType(OrdersConstants.GENERAL_ORDERS);
        assertEquals(2, generalOrdersRevenue.getFirst().totalRevenue().runningOrderCount());
        assertEquals(new BigDecimal("54.00"), generalOrdersRevenue.getFirst().totalRevenue().runningRevenue());
        OffsetDateTime expectedStartTime = OffsetDateTime.parse("2023-02-21T21:25:00+01:00");
        OffsetDateTime expectedEndTime = OffsetDateTime.parse("2023-02-21T21:25:15+01:00");
        Assertions.assertTrue(expectedStartTime.isEqual(generalOrdersRevenue.getFirst().startWindow()));
        Assertions.assertTrue(expectedEndTime.isEqual(generalOrdersRevenue.getFirst().endWindow()));

        List<OrdersRevenuePerStoreByWindowsDTO> restaurantOrdersRevenue = orderWindowService.getOrdersRevenueWindowByType(OrdersConstants.RESTAURANT_ORDERS);
        assertEquals(2, restaurantOrdersRevenue.getFirst().totalRevenue().runningOrderCount());
        assertEquals(new BigDecimal("30.00"), restaurantOrdersRevenue.getFirst().totalRevenue().runningRevenue());
    }

    private void publishOrders() {
        orders().forEach(order -> {
            kafkaTemplate.send(OrdersConstants.ORDERS, order.key, order.value);
        });
    }

    static List<KeyValue<String, Order>> orders() {

        var orderItems = List.of(
            new OrderLineItem("Bananas", 2, new BigDecimal("2.00")),
            new OrderLineItem("Iphone Charger", 1, new BigDecimal("25.00"))
        );

        var orderItemsRestaurant = List.of(
            new OrderLineItem("Pizza", 2, new BigDecimal("12.00")),
            new OrderLineItem("Coffee", 1, new BigDecimal("3.00"))
        );

        var order1 = new Order(12345L, "store_1234",
            new BigDecimal("27.00"),
            OrderType.GENERAL,
            orderItems,
            OffsetDateTime.parse("2023-02-21T21:25:01+01:00")
        );

        var order2 = new Order(54321L, "store_1234",
            new BigDecimal("15.00"),
            OrderType.RESTAURANT,
            orderItemsRestaurant,
            OffsetDateTime.parse("2023-02-21T21:25:01+01:00")
        );

        var keyValue1 = KeyValue.pair(order1.id().toString(), order1);
        var keyValue2 = KeyValue.pair(order2.id().toString(), order2);

        return List.of(keyValue1, keyValue2);
    }

}
