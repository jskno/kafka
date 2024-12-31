package com.jskno.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record TransactionPattern(
        String zipCode,
        String item,
        OffsetDateTime date,
        BigDecimal amount
) {
}
