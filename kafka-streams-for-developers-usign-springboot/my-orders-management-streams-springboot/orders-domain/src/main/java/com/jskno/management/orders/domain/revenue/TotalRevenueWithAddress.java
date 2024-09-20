package com.jskno.management.orders.domain.revenue;

import com.jskno.management.orders.domain.store.Store;

public record TotalRevenueWithAddress(

    TotalRevenue totalRevenue,
    Store store
) {
}
