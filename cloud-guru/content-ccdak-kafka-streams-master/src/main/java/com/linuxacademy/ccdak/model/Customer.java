package com.linuxacademy.ccdak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
{
  "customer_id": 493,
  "first_name": "Joan",
  "last_name": "Forlonge",
  "email": "jforlongedo@usatoday.com",
  "gender": "Female",
  "income": 365601,
  "fico": 827,
  "years_active": 42
}
 */
public class Customer {

    @JsonProperty("customer_id")
    Long customerId;
    @JsonProperty("first_name")
    String firstName;
    @JsonProperty("last_name")
    String lastName;
    String email;
    String gender;
    Integer income;
    Integer fico;
    @JsonProperty("years_active")
    Integer yearsActive;

    public Customer() {
    }

    public Customer(Long customerId, String firstName, String lastName, String email, String gender, Integer income, Integer fico,
        Integer yearsActive) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.income = income;
        this.fico = fico;
        this.yearsActive = yearsActive;
    }

    public Customer(Customer customer, Integer newIncome) {
        this(customer.getCustomerId(), customer.getFirstName().toUpperCase(), customer.getLastName().toUpperCase(),
            customer.getEmail(), customer.getGender(), newIncome, customer.getFico(), customer.getYearsActive());
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getIncome() {
        return income;
    }

    public void setIncome(Integer income) {
        this.income = income;
    }

    public Integer getFico() {
        return fico;
    }

    public void setFico(Integer fico) {
        this.fico = fico;
    }

    public Integer getYearsActive() {
        return yearsActive;
    }

    public void setYearsActive(Integer yearsActive) {
        this.yearsActive = yearsActive;
    }

    @Override
    public String toString() {
        return "Customer{" +
            "customerId=" + customerId +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", email='" + email + '\'' +
            ", gender='" + gender + '\'' +
            ", income=" + income +
            ", fico=" + fico +
            ", yearsActive=" + yearsActive +
            '}';
    }
}
