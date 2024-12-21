package com.jskno.core.dto.events;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductReservationFailedEvent {

    private UUID productId;
    private UUID orderId;
    private Integer productQuantity;

}
