package com.jskno.topology.c_ktable_join;

import com.jskno.domain.order.Order;
import com.jskno.domain.order.OrderType;
import com.jskno.domain.store.Store;
import com.jskno.domain.count.TotalCountWithAddress;
import com.jskno.domain.revenue.TotalRevenue;
import com.jskno.domain.revenue.TotalRevenueWithAddress;
import com.jskno.serdes.SerdesFactory;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.state.KeyValueStore;

public class OrderTopologyV5 {

    public static final String ORDERS = "orders";
    public static final String GENERAL_ORDERS = "general-orders";
    public static final String GENERAL_ORDERS_COUNT = "general-orders-count";
    public static final String GENERAL_ORDERS_COUNT_STORE = "general-orders-count-by-store";
    public static final String GENERAL_ORDERS_REVENUE = "general-orders-revenue";
    public static final String GENERAL_ORDERS_REVENUE_STORE = "general-orders-revenue-by-store";
    public static final String RESTAURANT_ORDERS = "restaurant-orders";
    public static final String RESTAURANT_ORDERS_COUNT = "restaurant-orders-count";
    public static final String RESTAURANT_ORDERS_COUNT_STORE = "restaurant-orders-count-by-store";
    public static final String RESTAURANT_ORDERS_REVENUE = "restaurant-orders-revenue";
    public static final String RESTAURANT_ORDERS_REVENUE_STORE = "restaurant-orders-revenue-by-store";
    public static final String STORES = "stores";

    public static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // KStream
        KStream<String, Order> ordersStream = streamsBuilder
            .stream(ORDERS, Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)))
            .selectKey((s, order) -> order.locationId());

        ordersStream.print(Printed.<String, Order>toSysOut().withLabel("orders"));

        // KTable
        KTable<String, Store> storeKTable = streamsBuilder.table(
            STORES,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Store.class)));

        ordersStream
            .split(Named.as("general-restaurant-streams"))
            .branch((key, order) -> OrderType.GENERAL == order.orderType(),
                Branched.withConsumer(generalStream -> {
                    generalStream.print(Printed.<String, Order>toSysOut().withLabel("generalStream"));
                    aggregateOrdersByCount(generalStream, GENERAL_ORDERS_COUNT, storeKTable, GENERAL_ORDERS_COUNT_STORE);
                    aggregateOrdersByRevenue(
                        generalStream, storeKTable, GENERAL_ORDERS_REVENUE, GENERAL_ORDERS_REVENUE_STORE);

                }))
            .branch(restaurantPredicate,
                Branched.withConsumer(restaurantStream -> {
                    restaurantStream.print(Printed.<String, Order>toSysOut().withLabel("restaurantStream"));
                    aggregateOrdersByCount(restaurantStream, RESTAURANT_ORDERS_COUNT, storeKTable, RESTAURANT_ORDERS_COUNT_STORE);
                    aggregateOrdersByRevenue(
                        restaurantStream, storeKTable, RESTAURANT_ORDERS_REVENUE, RESTAURANT_ORDERS_REVENUE_STORE);
                })
            );

        return streamsBuilder.build();
    }

    private static final Predicate<String, Order> generalPredicate = (s, order) -> OrderType.GENERAL == order.orderType();
    private static final Predicate<String, Order> restaurantPredicate = (s, order) -> OrderType.RESTAURANT == order.orderType();
    private static void aggregateOrdersByCount(KStream<String, Order> ordersStream, String ordersStore,
        KTable<String, Store> storeKTable, String countStore) {

        KTable<String, Long> ordersCountPerStore = ordersStream
            .groupByKey(Grouped.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)))
            .count(Named.as(ordersStore), Materialized.as(ordersStore));
        ordersCountPerStore.toStream().print(Printed.<String, Long>toSysOut().withLabel(ordersStore));

        ValueJoiner<Long, Store, TotalCountWithAddress> valueJoiner = TotalCountWithAddress::new;

        KTable<String, TotalCountWithAddress> countWithAddress = ordersCountPerStore.join(storeKTable, valueJoiner);

        countWithAddress.toStream().print(Printed.<String, TotalCountWithAddress>toSysOut().withLabel(countStore));
    }

    private static void aggregateOrdersByRevenue(KStream<String, Order> ordersStream,
        KTable<String, Store> storeKTable, String ordersStore, String revenueStore) {
        Initializer<TotalRevenue> initializer = TotalRevenue::new;

        Aggregator<String, Order, TotalRevenue> aggregator = (k, v, aggregate) -> aggregate.updateRunningRevenue(k, v);

        KTable<String, TotalRevenue> revenueTable = ordersStream
            .groupByKey(Grouped.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)))
            .aggregate(initializer, aggregator, Materialized.<String, TotalRevenue, KeyValueStore<Bytes, byte[]>>as(ordersStore)
                .withKeySerde(Serdes.String())
                .withValueSerde(SerdesFactory.jsonSerdes(TotalRevenue.class))
            );

        // Ktable-KTable join
        ValueJoiner<TotalRevenue, Store, TotalRevenueWithAddress> valueJoiner = TotalRevenueWithAddress::new;

        KTable<String, TotalRevenueWithAddress> revenueWithStoreTable = revenueTable.join(storeKTable, valueJoiner);

        revenueWithStoreTable.toStream().print(Printed.<String, TotalRevenueWithAddress>toSysOut().withLabel(revenueStore));

    }
}
