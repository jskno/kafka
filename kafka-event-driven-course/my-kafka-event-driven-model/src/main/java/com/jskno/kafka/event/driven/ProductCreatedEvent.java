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

    private String id;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
