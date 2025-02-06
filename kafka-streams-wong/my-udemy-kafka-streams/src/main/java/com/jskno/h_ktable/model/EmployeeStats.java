package com.jskno.h_ktable.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class EmployeeStats {

    private String department;
    private double totalSalary;

    public EmployeeStats() {

    }
}
