package com.github.alexwang.kafka.streams.model;

import java.util.Objects;

public class TransactionReward {
    private String customerId;
    private double purchaseTotal;
    private int rewardPoints;
    private int totalPoints;

    public TransactionReward() {

    }

    private TransactionReward(String customerId, double purchaseTotal, int rewardPoints) {
        this.customerId = customerId;
        this.purchaseTotal = purchaseTotal;
        this.rewardPoints = rewardPoints;
        this.totalPoints = rewardPoints;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getPurchaseTotal() {
        return purchaseTotal;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public static Builder builder(Transaction transaction) {
        return new Builder(transaction);
    }

    public static Builder builder(TransactionReward transactionReward) {
        return new Builder(transactionReward);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionReward that = (TransactionReward) o;
        return Double.compare(that.getPurchaseTotal(), getPurchaseTotal()) == 0 && getRewardPoints() == that.getRewardPoints()
                && getTotalPoints() == that.getTotalPoints() && Objects.equals(getCustomerId(), that.getCustomerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCustomerId(), getPurchaseTotal(), getRewardPoints(), getTotalPoints());
    }

    public static final class Builder {
        private final String customerId;
        private final double purchaseTotal;
        private final int rewardPoints;

        private Builder(Transaction transaction) {
            this.customerId = transaction.getCustomerId();
            this.purchaseTotal = transaction.getPrice() * (double) transaction.getQuantity();
            this.rewardPoints = (int) purchaseTotal;
        }

        private Builder(TransactionReward transactionReward) {
            this.customerId = transactionReward.getCustomerId();
            this.purchaseTotal = transactionReward.getPurchaseTotal();
            this.rewardPoints = transactionReward.getRewardPoints();
        }

        public TransactionReward build() {
            return new TransactionReward(this.customerId, this.purchaseTotal, this.rewardPoints);
        }
    }
}
