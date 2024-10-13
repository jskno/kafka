package com.github.alexwang.kafka.streams.model;

import java.util.Objects;

public class SalesStats {
    private String department;
    private double totalAmount;
    private double averageAmount;
    private int count;

    public SalesStats() {
    }

    public SalesStats(String department, double totalAmount, double averageAmount, int count) {
        this.department = department;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
        this.count = count;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(double averageAmount) {
        this.averageAmount = averageAmount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesStats that = (SalesStats) o;
        return Double.compare(that.getTotalAmount(), getTotalAmount()) == 0
                && Double.compare(that.getAverageAmount(), getAverageAmount()) == 0 && getCount() == that.getCount()
                && Objects.equals(getDepartment(), that.getDepartment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDepartment(), getTotalAmount(), getAverageAmount(), getCount());
    }

    @Override
    public String toString() {
        return "SalesStats{" +
                "department='" + department + '\'' +
                ", totalAmount=" + totalAmount +
                ", averageAmount=" + averageAmount +
                ", count=" + count +
                '}';
    }
}
