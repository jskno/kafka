package com.jskno.d_stateful_aggregation.model;

public class Sales {

    private String username;
    private String department;
    private double salesAmount;
    private double totalSalesAmount;

    public Sales() {
    }

    public Sales(String username, String department, double salesAmount, double totalSalesAmount) {
        this.username = username;
        this.department = department;
        this.salesAmount = salesAmount;
        this.totalSalesAmount = totalSalesAmount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
    public String toString() {
        return "Sales{" +
                "userName='" + username + '\'' +
                ", department='" + department + '\'' +
                ", salesAmount=" + salesAmount +
                ", totalSalesAmount=" + totalSalesAmount +
                '}';
    }
}
