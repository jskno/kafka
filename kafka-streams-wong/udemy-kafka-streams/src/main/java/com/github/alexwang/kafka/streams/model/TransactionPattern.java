package com.github.alexwang.kafka.streams.model;

import java.util.Date;
import java.util.Objects;

public class TransactionPattern {
    private String zipCode;
    private String item;
    private Date date;
    private double amount;

    private TransactionPattern(Builder builder) {
        this.zipCode = builder.zipCode;
        this.item = builder.item;
        this.date = builder.date;
        this.amount = builder.amount;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder builder(Transaction transaction) {
        return new Builder(transaction);
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionPattern that = (TransactionPattern) o;
        return Double.compare(that.amount, amount) == 0 && Objects.equals(zipCode, that.zipCode) && Objects.equals(item, that.item) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zipCode, item, date, amount);
    }

    public static final class Builder {
        private String zipCode;
        private String item;
        private Date date;
        private double amount;

        private Builder() {
        }

        private Builder(Transaction transaction) {
            this.zipCode = transaction.getZipCode();
            this.item = transaction.getItemPurchased();
            this.date = transaction.getPurchaseDate();
            this.amount = transaction.getPrice() * transaction.getQuantity();
        }

        public Builder zipCode(String val) {
            zipCode = val;
            return this;
        }

        public Builder item(String val) {
            item = val;
            return this;
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public TransactionPattern build() {
            return new TransactionPattern(this);
        }
    }
}
