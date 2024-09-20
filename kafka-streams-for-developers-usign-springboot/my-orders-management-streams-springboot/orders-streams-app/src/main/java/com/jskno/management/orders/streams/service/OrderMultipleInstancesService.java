package com.jskno.management.orders.streams.service;

import com.jskno.management.orders.domain.dto.AllOrderCountPerStoreDTO;
import com.jskno.management.orders.domain.dto.HostInfoDTO;
import com.jskno.management.orders.domain.dto.HostInfoWithKeyDTO;
import com.jskno.management.orders.domain.dto.OrderCountPerStoreDTO;
import com.jskno.management.orders.domain.order.OrderType;
import com.jskno.management.orders.streams.client.OrderServicesClient;
import com.jskno.management.orders.streams.constants.OrdersConstants;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderMultipleInstancesService {

    private final MetaDataService metaDataService;
    private final OrderServicesClient orderServicesClient;
    private final OrderService orderService;

    public List<OrderCountPerStoreDTO> getOrdersCountByType(String orderType) {
        List<OrderCountPerStoreDTO> ordersCountFromCurrentInstance = orderService.getOrdersCountByType(orderType);

        // 1. Fetch the metadata about other instances
        List<HostInfoDTO> streamsMetadata = metaDataService.getOthersHostStreamsMetadata();

        // 2. Make the REST call to get the data from the other instances
        //      Make sure the other instance is not going to make any network calls to other instances
        List<OrderCountPerStoreDTO> otherInstancesOrders = streamsMetadata.stream()
            .map(hostInfoDTO -> orderServicesClient.retrieveOrdersCountByOrderType(hostInfoDTO, orderType))
            .flatMap(Collection::stream)
            .toList();

        // 3. Aggregate the data
        return Stream.of(ordersCountFromCurrentInstance, otherInstancesOrders)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .toList();
    }

    public OrderCountPerStoreDTO getOrdersCountByLocationId(String orderType, String locationId) {
        String storeName = mapOrderCountStoreName(orderType);
        Optional<HostInfoWithKeyDTO> hostMetaData = metaDataService.getStreamsMetaData(storeName, locationId);
        if (hostMetaData.isPresent()) {
            if (hostMetaData.get().port() == metaDataService.getCurrentMachinePort()) {
                return orderService.getOrdersCountByLocationId(orderType, locationId);
            } else {
                return orderServicesClient.retrieveOrdersCountByOrderTypeAndLocationId(
                    hostMetaData.get(), orderType, locationId);
            }
        }
        return new OrderCountPerStoreDTO(locationId, null);
    }

    private String mapOrderCountStoreName(String orderType) {
        return switch (orderType) {
            case OrdersConstants.GENERAL_ORDERS ->OrdersConstants.GENERAL_ORDERS_COUNT;
            case OrdersConstants.RESTAURANT_ORDERS ->OrdersConstants.RESTAURANT_ORDERS_COUNT;
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

}
