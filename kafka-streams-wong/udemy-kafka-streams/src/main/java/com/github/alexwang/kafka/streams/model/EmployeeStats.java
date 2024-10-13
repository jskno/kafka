package com.github.alexwang.kafka.streams.model;

public class EmployeeStats {
    private String department;
    private double totalSalary;

    public EmployeeStats(String department, double totalSalary) {
        this.department = department;
        this.totalSalary = totalSalary;
    }

    public EmployeeStats() {
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    @Override
    public String toString() {
        return "EmployeeStats{" +
                "department='" + department + '\'' +
                ", totalSalary=" + totalSalary +
                '}';
    }
}
