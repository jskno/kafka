package com.jskno.domain.order;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record OrderLineItem(
    String item,
    Integer count,
    BigDecimal amount
) {
}
