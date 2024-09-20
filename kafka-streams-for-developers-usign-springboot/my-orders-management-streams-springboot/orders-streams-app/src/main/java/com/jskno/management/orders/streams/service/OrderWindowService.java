package com.jskno.management.orders.streams.service;

import static com.jskno.management.orders.streams.service.OrderService.mapOrderType;

import com.jskno.management.orders.domain.dto.OrderCountPerStoreByWindowDTO;
import com.jskno.management.orders.domain.dto.OrdersRevenuePerStoreByWindowsDTO;
import com.jskno.management.orders.domain.revenue.TotalRevenue;
import com.jskno.management.orders.streams.constants.OrdersConstants;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderWindowService {

    private final OrderStoreService orderStoreService;

    public List<OrderCountPerStoreByWindowDTO> getOrdersCountWindowByType(String orderType) {
        ReadOnlyWindowStore<String, Long> countWindowStore = getCountWindowStore(orderType);
        KeyValueIterator<Windowed<String>, Long> iterator = countWindowStore.all();
        return mapToOrdersCountPerStoreByWindowDTOS(orderType, iterator);
    }

    public List<OrderCountPerStoreByWindowDTO> getAllOrdersCountByWindow() {
        List<OrderCountPerStoreByWindowDTO> generalOrdersCountByWindow = getOrdersCountWindowByType(OrdersConstants.GENERAL_ORDERS);
        List<OrderCountPerStoreByWindowDTO> restaurantOrdersCountByWindow = getOrdersCountWindowByType(OrdersConstants.RESTAURANT_ORDERS);
        return Stream.of(generalOrdersCountByWindow, restaurantOrdersCountByWindow)
            .flatMap(Collection::stream)
            .toList();
    }

    private ReadOnlyWindowStore<String, Long> getCountWindowStore(String orderType) {
        return switch (orderType) {
            case OrdersConstants.GENERAL_ORDERS -> orderStoreService.orderWindowCountStore(OrdersConstants.GENERAL_ORDERS_COUNT_WINDOWS);
            case OrdersConstants.RESTAURANT_ORDERS -> orderStoreService.orderWindowCountStore(OrdersConstants.RESTAURANT_ORDERS_COUNT_WINDOWS);
            default -> throw new IllegalStateException("Unexpected value: " + orderType);
        };
    }

    private static List<OrderCountPerStoreByWindowDTO> mapToOrdersCountPerStoreByWindowDTOS(String orderType,
        KeyValueIterator<Windowed<String>, Long> iterator) {
        Spliterator<KeyValue<Windowed<String>, Long>> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, false)
            .map(keyValue -> new OrderCountPerStoreByWindowDTO(
                keyValue.key.key(), keyValue.value, mapOrderType(orderType),
                OffsetDateTime.ofInstant(keyValue.key.window().startTime(), ZoneId.of("GMT")),
                OffsetDateTime.ofInstant(keyValue.key.window().endTime(), ZoneId.of("GMT"))))
            .toList();
    }

    private static List<OrdersRevenuePerStoreByWindowsDTO> mapToOrdersRevenuePerStoreByWindowDTOS(String orderType,
        KeyValueIterator<Windowed<String>, TotalRevenue> iterator) {
        Spliterator<KeyValue<Windowed<String>, TotalRevenue>> spliterator = Spliterators.spliteratorUnknownSize(iterator, 0);
        return StreamSupport.stream(spliterator, false)
            .map(keyValue -> new OrdersRevenuePerStoreByWindowsDTO(
                keyValue.key.key(), keyValue.value, mapOrderType(orderType),
                OffsetDateTime.ofInstant(keyValue.key.window().startTime(), ZoneId.of("GMT")),
                OffsetDateTime.ofInstant(keyValue.key.window().endTime(), ZoneId.of("GMT"))))
            .toList();
    }

    public List<OrderCountPerStoreByWindowDTO> getAllOrdersCountByWindowAndTimes(OffsetDateTime fromTime, OffsetDateTime toTime) {
        KeyValueIterator<Windowed<String>, Long> generalOrdersCountByWindow = getCountWindowStore(
            OrdersConstants.GENERAL_ORDERS)
            .fetchAll(fromTime.toInstant(), toTime.toInstant());
        List<OrderCountPerStoreByWindowDTO> generalOrdersCountPerStoreByWindowsDTO = mapToOrdersCountPerStoreByWindowDTOS(
            OrdersConstants.GENERAL_ORDERS, generalOrdersCountByWindow);

        KeyValueIterator<Windowed<String>, Long> restaurantOrdersCountByWindow = getCountWindowStore(
            OrdersConstants.RESTAURANT_ORDERS)
            .fetchAll(fromTime.toInstant(), toTime.toInstant());
        List<OrderCountPerStoreByWindowDTO> restaurantOrdersCountPerStoreByWindowsDTO = mapToOrdersCountPerStoreByWindowDTOS(
            OrdersConstants.RESTAURANT_ORDERS, restaurantOrdersCountByWindow);

        return Stream.of(generalOrdersCountPerStoreByWindowsDTO, restaurantOrdersCountPerStoreByWindowsDTO)
            .flatMap(Collection::stream)
            .toList();
    }

    public List<OrdersRevenuePerStoreByWindowsDTO> getOrdersRevenueWindowByType(String orderType) {
        ReadOnlyWindowStore<String, TotalRevenue> revenueWindowStore = getRevenueWindowStore(orderType);
        KeyValueIterator<Windowed<String>, TotalRevenue> totalRevenueKeyValueIterator = revenueWindowStore.all();

        return mapToOrdersRevenuePerStoreByWindowDTOS(orderType, totalRevenueKeyValueIterator);
    }

    private ReadOnlyWindowStore<String, TotalRevenue> getRevenueWindowStore(String orderType) {
        return switch (orderType) {
            case OrdersConstants.GENERAL_ORDERS -> orderStoreService.getOrderWindowRevenueStore(OrdersConstants.GENERAL_ORDERS_REVENUE_WINDOWS);
            case OrdersConstants.RESTAURANT_ORDERS -> orderStoreService.getOrderWindowRevenueStore(OrdersConstants.RESTAURANT_ORDERS_REVENUE_WINDOWS);
            default -> throw new IllegalStateException("Unexpected value: " + orderType);
        };
    }
}
