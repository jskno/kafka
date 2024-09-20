package com.jskno.management.orders.domain.store;

public record Store(
    String locationId,
    Address address,
    String contactNum
) {

}
