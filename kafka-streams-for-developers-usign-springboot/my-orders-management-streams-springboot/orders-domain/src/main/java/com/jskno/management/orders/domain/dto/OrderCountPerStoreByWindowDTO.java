package com.jskno.management.orders.domain.dto;

import com.jskno.management.orders.domain.order.OrderType;
import java.time.OffsetDateTime;

public record OrderCountPerStoreByWindowDTO(
    String locationId,
    Long orderCount,
    OrderType orderType,
    OffsetDateTime startWindow,
    OffsetDateTime endWindow
) {

}
