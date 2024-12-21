package com.jskno.core.dto.events;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentProcessedEvent {

    private UUID orderId;
    private UUID paymentId;

}
