package com.jskno.domain.store;

public record Store(
    String locationId,
    Address address,
    String contactNum
) {

}
