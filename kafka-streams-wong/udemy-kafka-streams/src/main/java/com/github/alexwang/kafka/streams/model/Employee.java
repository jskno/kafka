package com.github.alexwang.kafka.streams.model;

public class Employee {
    private String empNo;
    private String name;
    private String department;
    private int age;
    private double salary;
    private String title;
    @Deprecated
    private double totalSalary;

    public Employee() {
    }

    public Employee(Builder builder) {
        this.empNo = builder.empNo;
        this.name = builder.name;
        this.department = builder.department;
        this.age = builder.age;
        this.salary = builder.salary;
        this.title = builder.title;
        this.totalSalary = builder.salary;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Employee employee) {
        final Builder builder = new Builder();
        builder.empNo = employee.empNo;
        builder.name = employee.name;
        builder.department = employee.department;
        builder.age = employee.age;
        builder.salary = employee.salary;
        builder.title = employee.title;
        return builder;
    }

    public String getEmpNo() {
        return empNo;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getAge() {
        return age;
    }

    public double getSalary() {
        return salary;
    }

    public String getTitle() {
        return title;
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setSalary(double salary) {
        this.salary = salary;
        //init
        this.totalSalary = salary;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "empNo='" + empNo + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", salary=" + salary +
                ", totalSalary=" + totalSalary +
                '}';
    }

    public static class Builder {
        private final static String HIGH = "HIGH";
        private final static String MEDIUM = "MEDIUM";
        private final static String LOW = "LOW";

        private String empNo;
        private String name;
        private String department;
        private int age;
        private double salary;
        private String title;

        public Builder empNo(String empNo) {
            this.empNo = empNo;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder salary(double salary) {
            this.salary = salary;
            return this;
        }

        private Builder() {
        }

        public Builder evaluateTitle() {
            if (this.salary >= 10000.D) {
                this.title = HIGH;
            } else if (this.salary < 10000.0D && this.salary >= 5000.D) {
                this.title = MEDIUM;
            } else {
                this.title = LOW;
            }
            return this;
        }

        public Employee build() {
            return new Employee(this);
        }
    }
}
