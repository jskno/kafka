package com.jskno.management.orders.domain.revenue;

import java.math.BigDecimal;

public record Revenue(
    String locationId,
    BigDecimal finalAmount
) {

}
