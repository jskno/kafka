package com.jskno.domain.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record Order(
    Long id,
    String locationId,
    BigDecimal finalAmount,
    OrderType orderType,
    List<OrderLineItem> lineItems,
    OffsetDateTime orderDateTime
) {

}
