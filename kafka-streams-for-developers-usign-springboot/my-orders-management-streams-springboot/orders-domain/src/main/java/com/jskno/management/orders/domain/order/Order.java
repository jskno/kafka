package com.jskno.management.orders.domain.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record Order(
    Long id,
    String locationId,
    BigDecimal finalAmount,
    OrderType orderType,
    List<OrderLineItem> lineItems,
    OffsetDateTime orderDateTime
) {

}
