package com.jskno.kafka.event.driven;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreatedEvent {

    // We name this a ProductCreatedEventV2
    private String id2;
    private String title2;
    private BigDecimal price2;
    private Long quantity2;
    private String description;
}
