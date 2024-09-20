package com.jskno.management.orders.domain.order;

import java.math.BigDecimal;

public record OrderLineItem(
    String item,
    Integer count,
    BigDecimal amount
) {
}
