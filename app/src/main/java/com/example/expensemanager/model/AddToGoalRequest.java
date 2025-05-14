package com.example.expensemanager.model;

public class AddToGoalRequest {
    private int goalId;
    private double amount;

    // Constructor
    public AddToGoalRequest(int goalId, double amount) {
        this.goalId = goalId;
        this.amount = amount;
    }

    // Empty constructor for Retrofit
    public AddToGoalRequest() {
    }

    // Getters and Setters
    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
