package com.jskno.domain.revenue;

import com.jskno.domain.revenue.TotalRevenue;
import com.jskno.domain.store.Store;

public record TotalRevenueWithAddress(

    TotalRevenue totalRevenue,
    Store store
) {
}
