package com.jskno.domain.revenue;

import com.jskno.domain.order.Order;
import java.math.BigDecimal;

public record TotalRevenue(
    String locationId,
    Integer runningOrderCount,
    BigDecimal runningRevenue
) {

    public TotalRevenue() {
        this("", 0, BigDecimal.valueOf(0.0));
    }

    public TotalRevenue updateRunningRevenue(String k, Order v) {
        var newOrdersCount = this.runningOrderCount + 1;
        var newRevenue = this.runningRevenue.add(v.finalAmount());
        return new TotalRevenue(k, newOrdersCount, newRevenue);

    }
}
