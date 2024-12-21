package com.jskno.core.dto.events;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderApprovedEvent {

    private UUID orderId;

}
