package com.jskno.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CustomerReward(
        String customerId,
        BigDecimal purchaseTotal,
        Integer rewardPoints,
        Integer totalPoints
) {
}
