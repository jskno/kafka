package com.github.alexwang.kafka.streams.model;

public class ShootStats {
    private String playerName;
    private int count;
    private int bestScore;
    private int lastScore;
    private String status;

    public ShootStats(){
        //for json serialization
    }

    private ShootStats(Builder builder) {
        this.playerName = builder.playerName;
        this.count = builder.count;
        this.bestScore = builder.bestScore;
        this.lastScore = builder.lastScore;
        this.status = builder.status;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public int getLastScore() {
        return lastScore;
    }

    public void setLastScore(int lastScore) {
        this.lastScore = lastScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Builder newBuilder(Shoot shoot) {
        return new Builder(shoot);
    }

    public static Builder newBuilder(String playerName) {
        return new Builder(playerName);
    }

    @Override
    public String toString() {
        return "ShootStats{" +
                "playerName='" + playerName + '\'' +
                ", count=" + count +
                ", bestScore=" + bestScore +
                ", lastScore=" + lastScore +
                ", status='" + status + '\'' +
                '}';
    }

    public static class Builder {
        private final String playerName;
        private final int count;
        private final int bestScore;
        private final int lastScore;
        private final String status;

        private Builder(Shoot shoot) {
            this.playerName = shoot.getPlayerName();
            this.bestScore = shoot.getScore();
            this.lastScore = shoot.getScore();
            this.count = 1;
            this.status = "PROGRESS";
        }

        private Builder(String playerName) {
            this.playerName = playerName;
            this.bestScore = -1;
            this.lastScore = -1;
            this.count = -1;
            this.status = "FINISHED";
        }

        public ShootStats build() {
            return new ShootStats(this);
        }
    }
}
