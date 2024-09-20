package com.jskno.coffee.orders.service.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineItemDTO {

    private String name;
    private SizeDTO size;
    private Integer quantity;
    private BigDecimal cost;


}
