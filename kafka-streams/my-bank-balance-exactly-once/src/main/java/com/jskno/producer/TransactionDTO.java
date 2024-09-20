package com.jskno.producer;

import java.time.OffsetDateTime;

public class TransactionDTO {
    private String name;
    private Double amount;
    private OffsetDateTime time;

    public TransactionDTO() {
    }

    public TransactionDTO(String name, Double amount, OffsetDateTime time) {
        this.name = name;
        this.amount = amount;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }
}
