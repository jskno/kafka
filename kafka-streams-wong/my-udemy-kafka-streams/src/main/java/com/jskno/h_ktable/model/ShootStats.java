package com.jskno.h_ktable.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ShootStats {
    private String playerName;
    private int count;
    private int bestScore;
    private int lastScore;
    private String status;

    private ShootStats(Builder builder) {
        this.playerName = builder.playerName;
        this.count = builder.count;
        this.bestScore = builder.bestScore;
        this.lastScore = builder.lastScore;
        this.status = builder.status;
    }

    public static Builder newBuilder(Shoot shoot) {
        return new Builder(shoot);
    }

    public static Builder newBuilder(String playerName) {
        return new Builder(playerName);
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
