package com.jskno.d_stateful_aggregation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesStats {

    private String department;
    private double totalAmount;
    private double averageAmount;
    private int count;

}
