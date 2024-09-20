package com.jskno.management.orders.domain.dto;

import com.jskno.management.orders.domain.order.OrderType;

public record AllOrderCountPerStoreDTO(
    String locationId,
    Long orderCount,
    OrderType orderType
) {

}
