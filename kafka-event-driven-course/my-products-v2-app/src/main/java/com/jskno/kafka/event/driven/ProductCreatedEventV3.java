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
public class ProductCreatedEventV3 {

    private String id;
    private String description;
    private BigDecimal price;
    private Long quantity;
}
