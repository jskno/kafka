package com.jskno.stream;

import java.time.OffsetDateTime;

public class BalanceDTO {

    private Integer count;
    private Double balance;
    private OffsetDateTime time;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }
}
