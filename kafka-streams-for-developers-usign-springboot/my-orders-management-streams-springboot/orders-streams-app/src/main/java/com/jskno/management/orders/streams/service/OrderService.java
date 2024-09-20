package com.jskno.management.orders.streams.service;

import com.jskno.management.orders.domain.dto.AllOrderCountPerStoreDTO;
import com.jskno.management.orders.domain.dto.OrderCountPerStoreDTO;
import com.jskno.management.orders.domain.dto.OrderRevenueDTO;
import com.jskno.management.orders.domain.order.OrderType;
import com.jskno.management.orders.domain.revenue.TotalRevenue;
import com.jskno.management.orders.streams.constants.OrdersConstants;
import java.util.Collection;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderStoreService orderStoreService;

    public List<OrderCountPerStoreDTO> getOrdersCountByType(String orderType) {
        ReadOnlyKeyValueStore<String, Long> orderCountStore = getOrderStore(orderType);
        KeyValueIterator<String, Long> orders = orderCountStore.all();
        Spliterator<KeyValue<String, Long>> keyValueSpliterator = Spliterators.spliteratorUnknownSize(orders, 0);
        return StreamSupport.stream(keyValueSpliterator, false)
            .map(keyValue -> new OrderCountPerStoreDTO(keyValue.key, keyValue.value))
            .toList();
    }

    public OrderCountPerStoreDTO getOrdersCountByLocationId(String orderType, String locationId) {
        ReadOnlyKeyValueStore<String, Long> orderCountStore = getOrderStore(orderType);
        Long count = orderCountStore.get(locationId);
        return new OrderCountPerStoreDTO(locationId, count);
    }

    public ReadOnlyKeyValueStore<String, Long> getOrderStore(String orderType) {
        return switch (orderType) {
            case OrdersConstants.GENERAL_ORDERS ->
                orderStoreService.ordersCountStore(OrdersConstants.GENERAL_ORDERS_COUNT);
            case OrdersConstants.RESTAURANT_ORDERS ->
                orderStoreService.ordersCountStore(OrdersConstants.RESTAURANT_ORDERS_COUNT);
            default -> throw new IllegalStateException("Unexpected value: " + orderType);
        };
    }

    public List<AllOrderCountPerStoreDTO> getAllOrdersCount() {

        BiFunction<OrderCountPerStoreDTO, OrderType, AllOrderCountPerStoreDTO> mapper =
            (orderCountPerStoreDTO, orderType) -> new AllOrderCountPerStoreDTO(
                orderCountPerStoreDTO.locationId(), orderCountPerStoreDTO.orderCount(), orderType);

        List<AllOrderCountPerStoreDTO> generalOrdersCount = getOrdersCountByType(OrdersConstants.GENERAL_ORDERS)
            .stream()
            .map(orderCountPerStoreDTO -> mapper.apply(orderCountPerStoreDTO, OrderType.GENERAL))
            .toList();

        List<AllOrderCountPerStoreDTO> restaurantOrdersCount = getOrdersCountByType(OrdersConstants.RESTAURANT_ORDERS)
            .stream()
            .map(orderCountPerStoreDTO -> mapper.apply(orderCountPerStoreDTO, OrderType.RESTAURANT))
            .toList();

        return Stream.of(generalOrdersCount, restaurantOrdersCount)
            .flatMap(Collection::stream)
            .toList();
    }

    public List<OrderRevenueDTO> getOrdersRevenueByType(String orderType) {

        ReadOnlyKeyValueStore<String, TotalRevenue> revenueStoreByType = getRevenueStore(orderType);
        KeyValueIterator<String, TotalRevenue> revenueIterator = revenueStoreByType.all();
        Spliterator<KeyValue<String, TotalRevenue>> spliterator = Spliterators.spliteratorUnknownSize(
            revenueIterator, 0);
        return StreamSupport.stream(spliterator, false)
            .map(keyValue -> new OrderRevenueDTO(keyValue.key, mapOrderType(orderType), keyValue.value))
            .toList();
    }

    public OrderRevenueDTO getOrdersRevenueByTypeAndLocationId(String orderType, String locationId) {
        ReadOnlyKeyValueStore<String, TotalRevenue> revenueStoreByType = getRevenueStore(orderType);
        TotalRevenue totalRevenue = revenueStoreByType.get(locationId);
        return new OrderRevenueDTO(locationId, mapOrderType(orderType), totalRevenue);
    }

    public static OrderType mapOrderType(String orderType) {
        return switch (orderType) {
            case OrdersConstants.GENERAL_ORDERS -> OrderType.GENERAL;
            case OrdersConstants.RESTAURANT_ORDERS -> OrderType.RESTAURANT;
            default -> throw new IllegalStateException("Unexpected value: " + orderType);
        };
    }

    private ReadOnlyKeyValueStore<String, TotalRevenue> getRevenueStore(String orderType) {
        return switch (orderType) {
            case OrdersConstants.GENERAL_ORDERS ->
                orderStoreService.ordersRevenueStore(OrdersConstants.GENERAL_ORDERS_REVENUE);
            case OrdersConstants.RESTAURANT_ORDERS ->
                orderStoreService.ordersRevenueStore(OrdersConstants.RESTAURANT_ORDERS_REVENUE);
            default -> throw new IllegalStateException("Unexpected value: " + orderType);
        };
    }
}
