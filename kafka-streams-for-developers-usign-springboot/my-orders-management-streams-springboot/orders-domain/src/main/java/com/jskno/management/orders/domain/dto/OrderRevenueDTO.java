package com.jskno.management.orders.domain.dto;

import com.jskno.management.orders.domain.order.OrderType;
import com.jskno.management.orders.domain.revenue.TotalRevenue;

public record OrderRevenueDTO(
    String locationId,
    OrderType orderType,
    TotalRevenue totalRevenue
) {

}
