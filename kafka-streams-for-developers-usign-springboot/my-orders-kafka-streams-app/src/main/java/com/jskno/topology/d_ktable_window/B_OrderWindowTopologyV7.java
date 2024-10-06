package com.jskno.topology.d_ktable_window;

import com.jskno.domain.order.Order;
import com.jskno.domain.store.Store;
import com.jskno.domain.count.TotalCountWithAddress;
import com.jskno.domain.revenue.TotalRevenue;
import com.jskno.domain.revenue.TotalRevenueWithAddress;
import com.jskno.serdes.SerdesFactory;
import com.jskno.utils.OrderTimestampExtractor;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.WindowStore;

@Slf4j
public class B_OrderWindowTopologyV7 {

    public static final String ORDERS_TOPIC = "orders";
    public static final String STORES_TOPIC = "stores";
    public static final String ORDERS_COUNT_STORE_BY_WINDOW = "orders-count-store-by-window";
    public static final String ORDERS_COUNT_STORE_ADDRESS_BY_WINDOW = "orders-count-store-address-by-window";
    public static final String ORDERS_REVENUE_STORE_BY_WINDOW = "orders-revenue-store-by-window";
    public static final String ORDERS_REVENUE_STORE_ADDRESS_BY_WINDOW = "general-orders-revenue-address-by-window";

    public static Topology build() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // KStream
        KStream<String, Order> ordersStream = streamsBuilder
            .stream(
                ORDERS_TOPIC,
                Consumed
                    .with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class))
                    .withTimestampExtractor(new OrderTimestampExtractor()))
            .selectKey((k, v) -> v.locationId());

        ordersStream.print(Printed.<String, Order>toSysOut().withLabel("orders"));

        // KTable
        KTable<String, Store> storesTable = streamsBuilder
            .table(STORES_TOPIC, Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Store.class)));

        aggregateOrdersByTimeWindow(ordersStream, storesTable);
        aggregateRevenueByTimeWindow(ordersStream, storesTable);

        return streamsBuilder.build();
    }

    private static void aggregateOrdersByTimeWindow(KStream<String, Order> ordersStream, KTable<String, Store> storesTable) {
        Duration windowSize = Duration.ofSeconds(15);
        TimeWindows timeWindows = TimeWindows.ofSizeWithNoGrace(windowSize);

        KTable<Windowed<String>, Long> ordersCountPerStore = ordersStream
            .groupByKey(Grouped.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)))
            .windowedBy(timeWindows)
            .count(Named.as(ORDERS_COUNT_STORE_BY_WINDOW), Materialized.as(ORDERS_COUNT_STORE_BY_WINDOW))
            .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded().shutDownWhenFull()));

        ordersCountPerStore.toStream()
            .peek(B_OrderWindowTopologyV7::printLocalDateTimes)
            .print(Printed.<Windowed<String>, Long>toSysOut().withLabel(ORDERS_COUNT_STORE_BY_WINDOW));

        ValueJoiner<Long, Store, TotalCountWithAddress> valueJoiner = TotalCountWithAddress::new;
        Joined<String, Long, Store> joinedParams = Joined.with(
            Serdes.String(),
            Serdes.Long(),
            SerdesFactory.jsonSerdes(Store.class));

        KStream<String, TotalCountWithAddress> ordersCountWithStoreTable = ordersCountPerStore
            .toStream()
            .map((k, v) -> KeyValue.pair(k.key(), v))
            .join(storesTable, valueJoiner, joinedParams);

        ordersCountWithStoreTable.print(Printed.<String, TotalCountWithAddress>toSysOut().withLabel(ORDERS_COUNT_STORE_ADDRESS_BY_WINDOW));
    }

    private static void aggregateRevenueByTimeWindow(KStream<String, Order> ordersStream, KTable<String, Store> storesTable) {
        Duration windowSize = Duration.ofSeconds(15);
        TimeWindows timeWindows = TimeWindows.ofSizeWithNoGrace(windowSize);

        Initializer<TotalRevenue> initializer = TotalRevenue::new;
        Aggregator<String, Order, TotalRevenue> aggregator = (k, v, aggregate) -> aggregate.updateRunningRevenue(k, v);

        KTable<Windowed<String>, TotalRevenue> ordersRevenueByWindow = ordersStream
            .groupByKey(Grouped.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)))
            .windowedBy(timeWindows)
            .aggregate(
                initializer,
                aggregator,
                Materialized.<String, TotalRevenue, WindowStore<Bytes, byte[]>>as(ORDERS_REVENUE_STORE_BY_WINDOW)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(SerdesFactory.jsonSerdes(TotalRevenue.class)));

        ordersRevenueByWindow
            .toStream()
            .peek(B_OrderWindowTopologyV7::printLocalDateTimes)
            .print(Printed.<Windowed<String>, TotalRevenue>toSysOut().withLabel(ORDERS_REVENUE_STORE_BY_WINDOW));

        // KTable-KTable join
        ValueJoiner<TotalRevenue, Store, TotalRevenueWithAddress> valueJoiner = TotalRevenueWithAddress::new;

        Joined<String, TotalRevenue, Store> joinedParams = Joined.with(
            Serdes.String(),
            SerdesFactory.jsonSerdes(TotalRevenue.class),
            SerdesFactory.jsonSerdes(Store.class));

        // KStream-KTable --> KStream
        KStream<String, TotalRevenueWithAddress> revenueWithStoreTable = ordersRevenueByWindow
            .toStream()
            .map((k,v) -> KeyValue.pair(k.key(), v))
            .join(storesTable, valueJoiner, joinedParams);

        revenueWithStoreTable.print(Printed.<String, TotalRevenueWithAddress>toSysOut().withLabel(ORDERS_REVENUE_STORE_ADDRESS_BY_WINDOW));

    }

    private static void printLocalDateTimes(Windowed<String> key, Object value) {
        var startTime = key.window().startTime();
        var endTime = key.window().endTime();
        log.info("startTime: {}, endTime: {}, Count: {}", startTime, endTime, value);

        LocalDateTime startLDT = LocalDateTime.ofInstant(startTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        LocalDateTime endLDT = LocalDateTime.ofInstant(endTime, ZoneId.of(ZoneId.SHORT_IDS.get("CST")));
        log.info("TumblingWindows: key {}, value: {}", key, value);
        log.info("startLDT: {}, endLDT: {}, Count: {}", startLDT, endLDT, value);
    }

}
