package com.jskno.topology.b_ktable_aggregate;

import com.jskno.domain.order.Order;
import com.jskno.domain.order.OrderLineItem;
import com.jskno.domain.order.OrderType;
import com.jskno.domain.revenue.TotalRevenue;
import com.jskno.serdes.SerdesFactory;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderTopologyV3Test {

    TopologyTestDriver topologyTestDriver;
    TestInputTopic<String, Order> inputTopic;

    @BeforeEach
    void setUp() {
        topologyTestDriver = new TopologyTestDriver(OrderTopologyV3.buildTopology());
        inputTopic = topologyTestDriver.createInputTopic(
            OrderTopologyV3.ORDERS,
            Serdes.String().serializer(),
            SerdesFactory.jsonSerdes(Order.class).serializer());
    }

    @AfterEach
    void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    void ordersCountTest() {
        // Given
        inputTopic.pipeKeyValueList(orders());

        // When
        ReadOnlyKeyValueStore<String, Long> generalOrdersCountStore =
            topologyTestDriver.getKeyValueStore(OrderTopologyV3.GENERAL_ORDERS_COUNT);
        KeyValueStore<Object, Object> restaurantOrdersCountStore =
            topologyTestDriver.getKeyValueStore(OrderTopologyV3.RESTAURANT_ORDERS_COUNT);

        // Then
        Assertions.assertEquals(1L, generalOrdersCountStore.get("store_1234"));
        Assertions.assertEquals(1L, restaurantOrdersCountStore.get("store_1234"));
    }

    @Test
    void ordersRevenueTest() {
        // Given
        inputTopic.pipeKeyValueList(orders());

        // When
        ReadOnlyKeyValueStore<String, TotalRevenue> generalOrdersRevenueStore =
            topologyTestDriver.getKeyValueStore(OrderTopologyV3.GENERAL_ORDERS_REVENUE);
        KeyValueStore<String, TotalRevenue> restaurantOrdersRevenueStore =
            topologyTestDriver.getKeyValueStore(OrderTopologyV3.RESTAURANT_ORDERS_REVENUE);

        // Then
        Assertions.assertEquals(1, generalOrdersRevenueStore.get("store_1234").runningOrderCount());
        Assertions.assertEquals(new BigDecimal("27.00"), generalOrdersRevenueStore.get("store_1234").runningRevenue());
        Assertions.assertEquals(1, restaurantOrdersRevenueStore.get("store_1234").runningOrderCount());
        Assertions.assertEquals(new BigDecimal("15.00"), restaurantOrdersRevenueStore.get("store_1234").runningRevenue());
    }

    @Test
    void ordersRevenueTest_WhenMultipleOrders() {
        // Given
        inputTopic.pipeKeyValueList(orders());
        inputTopic.pipeKeyValueList(orders());

        // When
        ReadOnlyKeyValueStore<String, TotalRevenue> generalOrdersRevenueStore =
            topologyTestDriver.getKeyValueStore(OrderTopologyV3.GENERAL_ORDERS_REVENUE);
        KeyValueStore<String, TotalRevenue> restaurantOrdersRevenueStore =
            topologyTestDriver.getKeyValueStore(OrderTopologyV3.RESTAURANT_ORDERS_REVENUE);

        // Then
        Assertions.assertEquals(2, generalOrdersRevenueStore.get("store_1234").runningOrderCount());
        Assertions.assertEquals(new BigDecimal("54.00"), generalOrdersRevenueStore.get("store_1234").runningRevenue());
        Assertions.assertEquals(2, restaurantOrdersRevenueStore.get("store_1234").runningOrderCount());
        Assertions.assertEquals(new BigDecimal("30.00"), restaurantOrdersRevenueStore.get("store_1234").runningRevenue());
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
            OffsetDateTime.parse("2023-02-21T21:25:01Z")
        );

        var order2 = new Order(54321L, "store_1234",
            new BigDecimal("15.00"),
            OrderType.RESTAURANT,
            orderItemsRestaurant,
            OffsetDateTime.parse("2023-02-21T21:25:01Z")
        );

        var keyValue1 = KeyValue.pair( order1.id().toString(), order1);
        var keyValue2 = KeyValue.pair( order2.id().toString(), order2);

        return  List.of(keyValue1, keyValue2);

    }



}
