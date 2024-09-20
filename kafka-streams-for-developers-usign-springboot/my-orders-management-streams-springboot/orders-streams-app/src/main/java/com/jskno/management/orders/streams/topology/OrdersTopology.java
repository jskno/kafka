package com.jskno.management.orders.streams.topology;

import com.jskno.management.orders.domain.order.Order;
import com.jskno.management.orders.domain.order.OrderType;
import com.jskno.management.orders.domain.revenue.TotalRevenue;
import com.jskno.management.orders.domain.revenue.TotalRevenueWithAddress;
import com.jskno.management.orders.domain.store.Store;
import com.jskno.management.orders.streams.constants.OrdersConstants;
import com.jskno.management.orders.streams.utils.OrderTimeStampExtractor;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrdersTopology {

    @Autowired
    public void process(StreamsBuilder streamsBuilder) {
        orderTopology(streamsBuilder);
    }

    private static void orderTopology(StreamsBuilder streamsBuilder) {
        KStream<String, Order> ordersStream = streamsBuilder.stream(
            OrdersConstants.ORDERS,
            Consumed
                .with(Serdes.String(), new JsonSerde<>(Order.class))
                .withTimestampExtractor(new OrderTimeStampExtractor()));
//            .selectKey((k, v) -> v.locationId());


        KTable<String, Store> storesTable = streamsBuilder.table(
            OrdersConstants.STORES,
            Consumed.with(Serdes.String(), new JsonSerde<>(Store.class)));

        ordersStream.print(Printed.<String, Order>toSysOut().withLabel(OrdersConstants.ORDERS));
        storesTable.toStream().print(Printed.<String, Store>toSysOut().withLabel(OrdersConstants.STORES));

        ordersStream
            .split(Named.as("general-restaurant-orders"))
            .branch((key, order) -> OrderType.GENERAL.equals(order.orderType()),
                Branched.withConsumer(generalOrdersStream -> {
                    generalOrdersStream.print(Printed.<String, Order>toSysOut().withLabel("generalStream"));

                    aggregateOrdersByCount(generalOrdersStream, OrdersConstants.GENERAL_ORDERS_COUNT);
                    aggregateOrdersCountByTimeWindows(generalOrdersStream, OrdersConstants.GENERAL_ORDERS_COUNT_WINDOWS);
                    aggregateOrdersByRevenue(generalOrdersStream, OrdersConstants.GENERAL_ORDERS_REVENUE, storesTable);
                    aggregateOrdersRevenueByWindows(generalOrdersStream, OrdersConstants.GENERAL_ORDERS_REVENUE_WINDOWS,
                        storesTable);
//                    aggregateOrdersCountByTimeWindowsWithGracePeriod(generalOrdersStream, OrdersConstants.GENERAL_ORDERS_COUNT_WINDOWS_GRACE);

                }))
            .branch((key, order) -> OrderType.RESTAURANT.equals(order.orderType()),
                Branched.withConsumer(restaurantOrdersStream -> {
                    restaurantOrdersStream.print(Printed.<String, Order>toSysOut().withLabel("restaurantStream"));

                    aggregateOrdersByCount(restaurantOrdersStream, OrdersConstants.RESTAURANT_ORDERS_COUNT);
                    aggregateOrdersCountByTimeWindows(restaurantOrdersStream, OrdersConstants.RESTAURANT_ORDERS_COUNT_WINDOWS);
                    aggregateOrdersByRevenue(restaurantOrdersStream, OrdersConstants.RESTAURANT_ORDERS_REVENUE, storesTable);
                    aggregateOrdersRevenueByWindows(restaurantOrdersStream, OrdersConstants.RESTAURANT_ORDERS_REVENUE_WINDOWS,
                        storesTable);
//                    aggregateOrdersCountByTimeWindowsWithGracePeriod(restaurantOrdersStream, OrdersConstants.RESTAURANT_ORDERS_COUNT_WINDOWS_GRACE);
                }));
    }


    private static void aggregateOrdersByCount(KStream<String, Order> generalOrdersStream, String storeName) {

        var generalOrdersCount = generalOrdersStream
            .map((key, value) -> KeyValue.pair(value.locationId(), value))
            .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
            .count(Named.as(storeName),
                Materialized.as(storeName));

        generalOrdersCount
            .toStream()
            .print(Printed.<String, Long>toSysOut().withLabel(storeName));

    }

    private static void aggregateOrdersCountByTimeWindows(KStream<String, Order> generalOrdersStream, String storeName) {

        Duration windowSize = Duration.ofSeconds(15);
        TimeWindows hoppingWindow = TimeWindows.ofSizeWithNoGrace(windowSize);

        var generalOrdersCount = generalOrdersStream
            .map((key, value) -> KeyValue.pair(value.locationId(), value))
            .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
            .windowedBy(hoppingWindow)
            .count(Named.as(storeName), Materialized.as(storeName))
//                .suppress(Suppressed
//                        .untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull())
//                )
            ;

        generalOrdersCount
            .toStream()
            .peek(((key, value) -> {
                log.info(" {} : tumblingWindow : key : {}, value : {}", storeName, key, value);
                printLocalDateTimes(key, value);
            }))
            .print(Printed.<Windowed<String>, Long>toSysOut().withLabel(storeName));
    }

    private static void aggregateOrdersCountByTimeWindowsWithGracePeriod(KStream<String, Order> generalOrdersStream, String storeName) {

        Duration windowSize = Duration.ofSeconds(60);
        Duration graceWindowsSize = Duration.ofSeconds(15);

        TimeWindows hoppingWindow = TimeWindows.ofSizeAndGrace(windowSize, graceWindowsSize);

        var generalOrdersCount = generalOrdersStream
            .map((key, value) -> KeyValue.pair(value.locationId(), value))
            .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
            .windowedBy(hoppingWindow)
            .count(Named.as(storeName), Materialized.as(storeName))
//                .suppress(Suppressed
//                        .untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull())
//                )
            ;

        generalOrdersCount
            .toStream()
            .peek(((key, value) -> {
                log.info(" {} : tumblingWindow : key : {}, value : {}", storeName, key, value);
                printLocalDateTimes(key, value);
            }))
            .print(Printed.<Windowed<String>, Long>toSysOut().withLabel(storeName));
    }

    private static void aggregateOrdersByRevenue(KStream<String, Order> generalOrdersStream, String aggregateStoreName,
        KTable<String, Store> storesTable) {

        Initializer<TotalRevenue> totalRevenueInitializer = TotalRevenue::new;

        Aggregator<String, Order, TotalRevenue> aggregator = (key, order, totalRevenue) -> {
            return totalRevenue.updateRunningRevenue(key, order);
        };

        var revenueTable = generalOrdersStream
            .map((key, value) -> KeyValue.pair(value.locationId(), value))
            .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
            .aggregate(totalRevenueInitializer,
                aggregator,
                Materialized
                    .<String, TotalRevenue, KeyValueStore<Bytes, byte[]>>as(aggregateStoreName)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new JsonSerde<>(TotalRevenue.class))
            );

        revenueTable
            .toStream()
            .print(Printed.<String, TotalRevenue>toSysOut().withLabel(aggregateStoreName));

        ValueJoiner<TotalRevenue, Store, TotalRevenueWithAddress> valueJoiner = TotalRevenueWithAddress::new;

        var revenueWithStoreTable = revenueTable
            .leftJoin(storesTable, valueJoiner);

        revenueWithStoreTable
            .toStream()
            .print(Printed.<String, TotalRevenueWithAddress>toSysOut().withLabel(aggregateStoreName + "-bystore"));

    }

    private static void aggregateOrdersRevenueByWindows(KStream<String, Order> generalOrdersStream, String aggregateStoreName,
        KTable<String, Store> storesTable) {

        var windowSize = 15;
        Duration windowSizeDuration = Duration.ofSeconds(windowSize);
        Duration graceWindowsSize = Duration.ofSeconds(5);

        TimeWindows hoppingWindow = TimeWindows.ofSizeAndGrace(windowSizeDuration, graceWindowsSize);

        Initializer<TotalRevenue> alphabetWordAggregateInitializer = TotalRevenue::new;

        Aggregator<String, Order, TotalRevenue> aggregator = (key, order, totalRevenue) -> {
            return totalRevenue.updateRunningRevenue(key, order);
        };

        var revenueTable = generalOrdersStream
            .map((key, value) -> KeyValue.pair(value.locationId(), value))
            .groupByKey(Grouped.with(Serdes.String(), new JsonSerde<>(Order.class)))
            .windowedBy(hoppingWindow)
            .aggregate(alphabetWordAggregateInitializer,
                aggregator
                , Materialized
                    .<String, TotalRevenue, WindowStore<Bytes, byte[]>>as(aggregateStoreName)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(new JsonSerde<>(TotalRevenue.class))
            );

        revenueTable
            .toStream()
            .peek(((key, value) -> {
                log.info(" {} : tumblingWindow : key : {}, value : {}", aggregateStoreName, key, value);
                printLocalDateTimes(key, value);
            }))
            .print(Printed.<Windowed<String>, TotalRevenue>toSysOut().withLabel(aggregateStoreName));
//
//        ValueJoiner<TotalRevenue, Store, TotalRevenueWithAddress> valueJoiner = TotalRevenueWithAddress::new;
//
//        revenueTable
//                .toStream()
//                .map((key, value) -> KeyValue.pair(key.key(), value))
//                .leftJoin(storesTable,valueJoiner)
//                .print(Printed.<String,TotalRevenueWithAddress>toSysOut().withLabel(aggregateStoreName+"-bystore"));

    }

    public static void printLocalDateTimes(Windowed<String> key, Object value) {
        var startTime = key.window().startTime();
        var endTime = key.window().endTime();
        log.info("startTime : {} , endTime : {}, Count : {}", startTime, endTime, value);
        LocalDateTime startLDT = LocalDateTime.ofInstant(startTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        LocalDateTime endLDT = LocalDateTime.ofInstant(endTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        log.info("startLDT : {} , endLDT : {}, Count : {}", startLDT, endLDT, value);
    }

}
