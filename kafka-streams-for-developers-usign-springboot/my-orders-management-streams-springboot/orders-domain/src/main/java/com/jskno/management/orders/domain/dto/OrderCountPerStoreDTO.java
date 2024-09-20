package com.jskno.management.orders.domain.dto;

public record OrderCountPerStoreDTO(
    String locationId,
    Long orderCount
) {

}
