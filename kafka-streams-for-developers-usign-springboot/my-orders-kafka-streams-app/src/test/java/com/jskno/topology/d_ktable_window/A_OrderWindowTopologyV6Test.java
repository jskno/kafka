package com.jskno.topology.d_ktable_window;

import com.jskno.domain.order.Order;
import com.jskno.domain.order.OrderLineItem;
import com.jskno.domain.order.OrderType;
import com.jskno.domain.revenue.TotalRevenue;
import com.jskno.serdes.SerdesFactory;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.state.WindowStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class A_OrderWindowTopologyV6Test {

    TopologyTestDriver topologyTestDriver;
    TestInputTopic<String, Order> inputTopic;

    @BeforeEach
    void setUp() {
        topologyTestDriver = new TopologyTestDriver(B_OrderWindowTopologyV7.build());
        inputTopic = topologyTestDriver.createInputTopic(
            A_OrderWindowTopologyV6.ORDERS_TOPIC,
            Serdes.String().serializer(),
            SerdesFactory.jsonSerdes(Order.class).serializer());
    }

    @AfterEach
    void tearDown() {
        topologyTestDriver.close();
    }

    @Test
    void ordersRevenueByWindowTest() {
        // Given
        inputTopic.pipeKeyValueList(orders());
        inputTopic.pipeKeyValueList(orders());

        // When
        WindowStore<String, TotalRevenue> ordersRevenueStore =
            topologyTestDriver.getWindowStore(B_OrderWindowTopologyV7.ORDERS_REVENUE_STORE_BY_WINDOW);

        // Then
        ordersRevenueStore.all().forEachRemaining(totalRevenueKeyValue -> {
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
            Assertions.assertEquals(4, totalRevenue.runningOrderCount());
            Assertions.assertEquals(new BigDecimal("84.00"), totalRevenue.runningRevenue());
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
