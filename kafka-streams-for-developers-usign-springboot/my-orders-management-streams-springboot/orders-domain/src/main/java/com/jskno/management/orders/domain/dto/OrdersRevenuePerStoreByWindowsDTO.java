package com.jskno.management.orders.domain.dto;

import com.jskno.management.orders.domain.order.OrderType;
import com.jskno.management.orders.domain.revenue.TotalRevenue;
import java.time.OffsetDateTime;

public record OrdersRevenuePerStoreByWindowsDTO(
    String locationId,
    TotalRevenue totalRevenue,
    OrderType orderType,
    OffsetDateTime startWindow,
    OffsetDateTime endWindow
) {

}
