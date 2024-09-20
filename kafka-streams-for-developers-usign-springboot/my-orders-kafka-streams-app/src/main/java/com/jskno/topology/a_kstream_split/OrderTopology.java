package com.jskno.topology.a_kstream_split;

import com.jskno.domain.order.Order;
import com.jskno.domain.order.OrderType;
import com.jskno.serdes.SerdesFactory;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;

public class OrderTopology {

    public static final String ORDERS = "orders";
    public static final String GENERAL_ORDERS = "general-orders";
    public static final String RESTAURANT_ORDERS = "restaurant-orders";
    public static final String STORES = "stores";

    public static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        KStream<String, Order> ordersStream = streamsBuilder.stream(
            ORDERS, Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)));

        ordersStream.print(Printed.<String, Order>toSysOut().withLabel("orders"));

        ordersStream
            .split(Named.as("general-restaurant-streams"))
            .branch((key, order) -> OrderType.GENERAL == order.orderType(),
                Branched.withConsumer(generalStream -> {
                    generalStream.print(Printed.<String, Order>toSysOut().withLabel("generalStream"));
                    generalStream.to(GENERAL_ORDERS, Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)));
                }))
            .branch((key, order) -> OrderType.RESTAURANT == order.orderType(),
                Branched.withConsumer(restaurantStream -> {
                    restaurantStream.print(Printed.<String, Order>toSysOut().withLabel("restaurantStream"));
                    restaurantStream.to(RESTAURANT_ORDERS, Produced.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)));
                })
            );


        return streamsBuilder.build();
    }
}
