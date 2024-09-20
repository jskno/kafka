package com.jskno.coffee.orders.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoffeeOrderCreateDTO {

    private OrderIdDTO id;
    private String name;
    private String nickName;
    private StoreDTO store;
    private List<OrderLineItemDTO> orderLineItems;
    private PickUpDTO pickUp;
    private CoffeeOrderStatusDTO status;
    private OffsetDateTime offsetDateTime;
    private LocalDateTime localDateTime;
    private LocalDate localDate;

}
