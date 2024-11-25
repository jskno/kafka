package com.jskno.productsapp.domain;

import java.time.OffsetDateTime;

public record ErrorMessage(
        OffsetDateTime timestam,
        ErrorCode code,
        String message
) {
}
