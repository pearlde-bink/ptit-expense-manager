package com.example.expensemanager.model.api;

public class GoalOverviewResponse {
    private int totalGoals;
    private int totalCurrentAmount;
    private int totalRemainingAmount;

    // Getters and Setters
    public int getTotalGoals() {
        return totalGoals;
    }

    public void setTotalGoals(int totalGoals) {
        this.totalGoals = totalGoals;
    }

    public int getTotalCurrentAmount() {
        return totalCurrentAmount;
    }

    public void setTotalCurrentAmount(int totalCurrentAmount) {
        this.totalCurrentAmount = totalCurrentAmount;
    }

    public int getTotalRemainingAmount() {
        return totalRemainingAmount;
    }

    public void setTotalRemainingAmount(int totalRemainingAmount) {
        this.totalRemainingAmount = totalRemainingAmount;
    }
}
