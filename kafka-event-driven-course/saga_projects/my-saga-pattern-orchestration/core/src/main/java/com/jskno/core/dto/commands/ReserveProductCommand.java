package com.jskno.core.dto.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReserveProductCommand {
    private UUID productId;
    private Integer productQuantity;
    private UUID orderId;

}
