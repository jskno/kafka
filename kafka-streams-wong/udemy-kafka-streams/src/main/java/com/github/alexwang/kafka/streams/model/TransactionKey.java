package com.github.alexwang.kafka.streams.model;

import java.util.Date;
import java.util.Objects;

public class TransactionKey
{
    private final String customerId;
    private final Date transactionDate;

    public TransactionKey(String customerId, Date transactionDate) {
        this.customerId = customerId;
        this.transactionDate = transactionDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionKey that = (TransactionKey) o;
        return Objects.equals(getCustomerId(), that.getCustomerId()) && Objects.equals(getTransactionDate(), that.getTransactionDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomerId(), getTransactionDate());
    }
}
