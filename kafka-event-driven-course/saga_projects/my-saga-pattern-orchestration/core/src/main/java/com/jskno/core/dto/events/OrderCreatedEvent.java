package com.jskno.core.dto.events;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent {

    private UUID orderId;
    private UUID customerId;
    private UUID productId;
    private Integer productQuantity;

}
