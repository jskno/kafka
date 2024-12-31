package com.jskno.orders.service.dto;

import com.jskno.core.types.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderHistory {

    private UUID id;
    private UUID orderId;
    private OrderStatus status;
    private Timestamp createdAt;
}