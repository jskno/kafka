package com.jskno.topology.b_ktable_aggregate;

import com.jskno.domain.order.Order;
import com.jskno.domain.order.OrderType;
import com.jskno.domain.revenue.TotalRevenue;
import com.jskno.serdes.SerdesFactory;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
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
import org.apache.kafka.streams.state.KeyValueStore;

public class OrderTopologyV3 {

    public static final String ORDERS = "orders";
    public static final String GENERAL_ORDERS = "general-orders";
    public static final String GENERAL_ORDERS_COUNT = "general-orders-count";
    public static final String GENERAL_ORDERS_REVENUE = "general-orders-revenue";
    public static final String RESTAURANT_ORDERS = "restaurant-orders";
    public static final String RESTAURANT_ORDERS_COUNT = "restaurant-orders-count";
    public static final String RESTAURANT_ORDERS_REVENUE = "restaurant-orders-revenue";
    public static final String STORES = "stores";

    public static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, Order> ordersStream = streamsBuilder.stream(
            ORDERS, Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class))
        );

        ordersStream.print(Printed.<String, Order>toSysOut().withLabel("orders"));

        ordersStream
            .split(Named.as("general-restaurant-streams"))
            .branch((key, order) -> OrderType.GENERAL == order.orderType(),
                Branched.withConsumer(generalStream -> {
                    generalStream.print(Printed.<String, Order>toSysOut().withLabel("generalStream"));
                    aggregateOrdersByCount(generalStream, GENERAL_ORDERS_COUNT);
                    aggregateOrdersByRevenue(generalStream, GENERAL_ORDERS_REVENUE);

                }))
            .branch(restaurantPredicate,
                Branched.withConsumer(restaurantStream -> {
                    restaurantStream.print(Printed.<String, Order>toSysOut().withLabel("restaurantStream"));
                    aggregateOrdersByCount(restaurantStream, RESTAURANT_ORDERS_COUNT);
                    aggregateOrdersByRevenue(restaurantStream, RESTAURANT_ORDERS_REVENUE);
                })
            );

        return streamsBuilder.build();
    }

    private static final Predicate<String, Order> generalPredicate = (s, order) -> OrderType.GENERAL == order.orderType();
    private static final Predicate<String, Order> restaurantPredicate = (s, order) -> OrderType.RESTAURANT == order.orderType();
    private static void aggregateOrdersByCount(KStream<String, Order> ordersStream, String ordersStore) {
        KTable<String, Long> ordersCountPerStore = ordersStream
            .map((k, v) -> KeyValue.pair(v.locationId(), v))
            .groupByKey(Grouped.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)))
            .count(Named.as(ordersStore), Materialized.as(ordersStore));
        ordersCountPerStore.toStream().print(Printed.<String, Long>toSysOut().withLabel(ordersStore));
    }

    private static void aggregateOrdersByRevenue(KStream<String, Order> ordersStream, String ordersStore) {
        Initializer<TotalRevenue> initializer = TotalRevenue::new;

        Aggregator<String, Order, TotalRevenue> aggregator = (k, v, aggregate) -> aggregate.updateRunningRevenue(k, v);

        KTable<String, TotalRevenue> revenueTable = ordersStream
            .map((k, v) -> KeyValue.pair(v.locationId(), v))
            .groupByKey(Grouped.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)))
            .aggregate(initializer, aggregator, Materialized.<String, TotalRevenue, KeyValueStore<Bytes, byte[]>>as(ordersStore)
                .withKeySerde(Serdes.String())
                .withValueSerde(SerdesFactory.jsonSerdes(TotalRevenue.class))
            );

        revenueTable.toStream().print(Printed.<String, TotalRevenue>toSysOut().withLabel(ordersStore));

    }
}
