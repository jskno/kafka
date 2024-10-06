package com.jskno.topology.e_my_exercises;

import com.jskno.constants.OrdersConstants;
import com.jskno.domain.order.Order;
import com.jskno.domain.order.OrderWithStoreDetails;
import com.jskno.domain.store.Store;
import com.jskno.serdes.SerdesFactory;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class A_KStreamJoinKTableTopology {

    public static Topology buildTopology() {
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        // KStream (re-key so that it matches with storeTable key)
        KStream<String, Order> ordersStreamStationCodeAsKey = streamsBuilder.stream(
            OrdersConstants.ORDERS_TOPIC,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Order.class)))
            .selectKey((originalKey, order) -> order.locationId());

        ordersStreamStationCodeAsKey.print(Printed.<String, Order>toSysOut().withLabel(OrdersConstants.ORDERS_TOPIC));

        // KTable (the store topic key is the locationId)
        KTable<String, Store> storeTable = streamsBuilder.table(
            OrdersConstants.STORES_TOPIC,
            Consumed.with(Serdes.String(), SerdesFactory.jsonSerdes(Store.class)));

        storeTable.toStream().print(Printed.<String, Store>toSysOut().withLabel(OrdersConstants.STORES_TOPIC));

        ValueJoiner<Order, Store, OrderWithStoreDetails> valueJoiner = OrderWithStoreDetails::new;
        Joined<String, Order, Store> joinedParams = Joined.with(
            Serdes.String(),
            SerdesFactory.jsonSerdes(Order.class),
            SerdesFactory.jsonSerdes(Store.class));

//        KStream<String, OrderWithStoreDetails> orderWithStoreDetailsKStream =
//            ordersStreamStationCodeAsKey.join(storeTable, valueJoiner, joinedParams);

        KStream<String, OrderWithStoreDetails> orderWithStoreDetailsKStream =
            ordersStreamStationCodeAsKey.leftJoin(storeTable, valueJoiner, joinedParams);


        orderWithStoreDetailsKStream
            .print(Printed.<String, OrderWithStoreDetails>toSysOut()
            .withLabel(OrdersConstants.ORDERS_WITH_STORE_DETAILS_STREAM));


        return streamsBuilder.build();
    }

}
