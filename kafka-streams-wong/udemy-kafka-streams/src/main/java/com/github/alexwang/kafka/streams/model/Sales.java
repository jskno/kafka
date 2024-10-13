package com.github.alexwang.kafka.streams.model;

import java.util.Objects;

public class Sales {
    private String userName;
    private String department;
    private double salesAmount;
    private double totalSalesAmount;

    public Sales() {

    }

    public Sales(Builder builder) {
        this.userName = builder.userName;
        this.department = builder.department;
        this.salesAmount = builder.salesAmount;
        this.totalSalesAmount = builder.totalSalesAmount;
    }

    public static Builder newBuild(Sales sales) {
        return new Builder(sales);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(double salesAmount) {
        this.salesAmount = salesAmount;
    }

    public double getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(double totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sales that = (Sales) o;
        return Double.compare(that.getSalesAmount(), getSalesAmount()) == 0 && Double.compare(that.getTotalSalesAmount(), getTotalSalesAmount()) == 0 && Objects.equals(getUserName(), that.getUserName()) && Objects.equals(getDepartment(), that.getDepartment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getDepartment(), getSalesAmount(), getTotalSalesAmount());
    }

    @Override
    public String toString() {
        return "Sales{" +
                "userName='" + userName + '\'' +
                ", department='" + department + '\'' +
                ", salesAmount=" + salesAmount +
                ", totalSalesAmount=" + totalSalesAmount +
                '}';
    }

    public static class Builder {
        private final String userName;
        private final String department;
        private final double salesAmount;
        private double totalSalesAmount;

        public Builder(Sales sales) {
            this.userName = sales.getUserName();
            this.department = sales.getDepartment();
            this.salesAmount = sales.getSalesAmount();
            this.totalSalesAmount = sales.getSalesAmount();
        }

        public Builder accumulateSalesAmount(Double historicalSalesAmount) {
            this.totalSalesAmount += historicalSalesAmount;
            return this;
        }

        public Sales build() {
            return new Sales(this);
        }
    }
}
