package com.jskno.management.orders.streams.topology;

import com.jskno.management.orders.domain.order.Order;
import com.jskno.management.orders.domain.order.OrderLineItem;
import com.jskno.management.orders.domain.order.OrderType;
import com.jskno.management.orders.domain.revenue.TotalRevenue;
import com.jskno.management.orders.streams.constants.OrdersConstants;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerde;

public class OrderTopologyTest {

    TopologyTestDriver topologyTestDriver;
    TestInputTopic<String, Order> inputTopic;
    StreamsBuilder streamsBuilder;

    @BeforeEach
    void setUp() {
        streamsBuilder = new StreamsBuilder();
        OrdersTopology ordersTopology = new OrdersTopology();
        ordersTopology.process(streamsBuilder);

        topologyTestDriver = new TopologyTestDriver(streamsBuilder.build());
        inputTopic = topologyTestDriver.createInputTopic(
            OrdersConstants.ORDERS,
            Serdes.String().serializer(),
            new JsonSerde<>(Order.class).serializer());
    }

    @AfterEach
    void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    void ordersCount() {
        // Given
        inputTopic.pipeKeyValueList(orders());

        // When
        ReadOnlyKeyValueStore<String, Long> generalOrdersKeyValueStore = topologyTestDriver.getKeyValueStore(OrdersConstants.GENERAL_ORDERS_COUNT);
        ReadOnlyKeyValueStore<String, Long> restaurantOrdersKeyValueStore = topologyTestDriver.getKeyValueStore(OrdersConstants.RESTAURANT_ORDERS_COUNT);

        // Then
        var generalOrdersCount = generalOrdersKeyValueStore.get("store_1234");
        Assertions.assertEquals(1, generalOrdersCount);

        var restaurantOrdersCount = restaurantOrdersKeyValueStore.get("store_1234");
        Assertions.assertEquals(1, restaurantOrdersCount);
    }

    @Test
    void ordersRevenue() {
        // Given
        inputTopic.pipeKeyValueList(orders());

        // When
        ReadOnlyKeyValueStore<String, TotalRevenue> generalOrdersRevenueStore = topologyTestDriver.getKeyValueStore(OrdersConstants.GENERAL_ORDERS_REVENUE);
        ReadOnlyKeyValueStore<String, TotalRevenue> restaurantOrdersRevenueStore = topologyTestDriver.getKeyValueStore(OrdersConstants.RESTAURANT_ORDERS_REVENUE);

        // Then
        TotalRevenue generalOrdersTotalRevenue = generalOrdersRevenueStore.get("store_1234");
        Assertions.assertEquals(1, generalOrdersTotalRevenue.runningOrderCount());
        Assertions.assertEquals(new BigDecimal("27.00"), generalOrdersTotalRevenue.runningRevenue());

        TotalRevenue restaurantOrdersRevenue = restaurantOrdersRevenueStore.get("store_1234");
        Assertions.assertEquals(1, restaurantOrdersRevenue.runningOrderCount());
        Assertions.assertEquals(new BigDecimal("15.00"), restaurantOrdersRevenue.runningRevenue());
    }

    @Test
    void ordersRevenue_WhenMultipleOrders() {
        // Given
        inputTopic.pipeKeyValueList(orders());
        inputTopic.pipeKeyValueList(orders());

        // When
        ReadOnlyKeyValueStore<String, TotalRevenue> generalOrdersRevenueStore = topologyTestDriver.getKeyValueStore(OrdersConstants.GENERAL_ORDERS_REVENUE);
        ReadOnlyKeyValueStore<String, TotalRevenue> restaurantOrdersRevenueStore = topologyTestDriver.getKeyValueStore(OrdersConstants.RESTAURANT_ORDERS_REVENUE);

        // Then
        TotalRevenue generalOrdersTotalRevenue = generalOrdersRevenueStore.get("store_1234");
        Assertions.assertEquals(2, generalOrdersTotalRevenue.runningOrderCount());
        Assertions.assertEquals(new BigDecimal("54.00"), generalOrdersTotalRevenue.runningRevenue());

        TotalRevenue restaurantOrdersRevenue = restaurantOrdersRevenueStore.get("store_1234");
        Assertions.assertEquals(2, restaurantOrdersRevenue.runningOrderCount());
        Assertions.assertEquals(new BigDecimal("30.00"), restaurantOrdersRevenue.runningRevenue());
    }

    @Test
    void ordersRevenueByWindowTest() {
        // Given
        inputTopic.pipeKeyValueList(orders());
        inputTopic.pipeKeyValueList(orders());

        // When
        WindowStore<String, TotalRevenue> generalOrdersRevenueStore =
            topologyTestDriver.getWindowStore(OrdersConstants.GENERAL_ORDERS_REVENUE_WINDOWS);
        WindowStore<String, TotalRevenue> restaurantOrdersRevenueStore =
            topologyTestDriver.getWindowStore(OrdersConstants.RESTAURANT_ORDERS_REVENUE_WINDOWS);

        // Then
        generalOrdersRevenueStore.all().forEachRemaining(totalRevenueKeyValue -> {
            Instant startTime = totalRevenueKeyValue.key.window().startTime();
            Instant endTime = totalRevenueKeyValue.key.window().endTime();

            System.out.println("startTime : " + startTime);
            System.out.println("endTime : " + endTime);

            OffsetDateTime expectedStartTime = OffsetDateTime.parse("2023-02-21T21:25:00+01:00");
            OffsetDateTime expectedEndTime = OffsetDateTime.parse("2023-02-21T21:25:15+01:00");

            Assertions.assertEquals(expectedStartTime,
                OffsetDateTime.ofInstant(startTime, ZoneId.of(ZoneId.SHORT_IDS.get("ECT"))));
            Assertions.assertEquals(expectedEndTime,
                OffsetDateTime.ofInstant(endTime, ZoneId.of(ZoneId.SHORT_IDS.get("ECT"))));

            var totalRevenue = totalRevenueKeyValue.value;
            Assertions.assertEquals(2, totalRevenue.runningOrderCount());
            Assertions.assertEquals(new BigDecimal("54.00"), totalRevenue.runningRevenue());
        });

        restaurantOrdersRevenueStore.all().forEachRemaining(totalRevenueKeyValue -> {
            Instant startTime = totalRevenueKeyValue.key.window().startTime();
            Instant endTime = totalRevenueKeyValue.key.window().endTime();

            System.out.println("startTime : " + startTime);
            System.out.println("endTime : " + endTime);

            OffsetDateTime expectedStartTime = OffsetDateTime.parse("2023-02-21T21:25:00+01:00");
            OffsetDateTime expectedEndTime = OffsetDateTime.parse("2023-02-21T21:25:15+01:00");

            Assertions.assertEquals(expectedStartTime,
                OffsetDateTime.ofInstant(startTime, ZoneId.of(ZoneId.SHORT_IDS.get("ECT"))));
            Assertions.assertEquals(expectedEndTime,
                OffsetDateTime.ofInstant(endTime, ZoneId.of(ZoneId.SHORT_IDS.get("ECT"))));

            var totalRevenue = totalRevenueKeyValue.value;
            Assertions.assertEquals(2, totalRevenue.runningOrderCount());
            Assertions.assertEquals(new BigDecimal("30.00"), totalRevenue.runningRevenue());
        });
    }

    static List<KeyValue<String, Order>> orders(){

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

        var keyValue1 = KeyValue.pair( order1.id().toString(), order1);
        var keyValue2 = KeyValue.pair( order2.id().toString(), order2);

        return  List.of(keyValue1, keyValue2);
    }



}
