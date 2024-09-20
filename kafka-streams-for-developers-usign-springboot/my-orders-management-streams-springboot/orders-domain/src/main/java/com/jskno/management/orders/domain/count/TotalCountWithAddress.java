package com.jskno.management.orders.domain.count;

import com.jskno.management.orders.domain.store.Store;

public record TotalCountWithAddress(
    Long count,
    Store store
) {

}
