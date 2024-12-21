package com.jskno.core.dto.events;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent {

    private UUID orderId;
    private UUID productId;
    private Integer productQuantity;

}
