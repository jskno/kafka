package com.jskno.model;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record TransactionKey(
        String customerId,
        OffsetDateTime transactionDate
) {
}
