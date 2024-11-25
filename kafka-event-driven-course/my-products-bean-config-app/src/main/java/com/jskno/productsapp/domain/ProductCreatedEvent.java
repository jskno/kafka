package com.jskno.productsapp.domain;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductCreatedEvent {

    private String id;
    private String title;
    private BigDecimal price;
    private Integer quantity;
}
