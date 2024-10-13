package com.github.alexwang.kafka.streams.model;

import java.util.Objects;

public class Shoot {
    private String playerName;
    private int score;

    public Shoot() {
    }

    public Shoot(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shoot shoot = (Shoot) o;
        return getScore() == shoot.getScore() && Objects.equals(getPlayerName(), shoot.getPlayerName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerName(), getScore());
    }

    @Override
    public String toString() {
        return "Shoot{" +
                "playerName='" + playerName + '\'' +
                ", score=" + score +
                '}';
    }
}
