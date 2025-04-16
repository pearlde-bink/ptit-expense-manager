package com.example.expensemanager.model;

import java.io.Serializable;

public class Goal implements Serializable {
    private int iconResId;
    private String title;
    private double currentAmount;
    private double targetAmount;

    public Goal(int iconResId, String title, double currentAmount, double targetAmount) {
        this.iconResId = iconResId;
        this.title = title;
        this.currentAmount = currentAmount;
        this.targetAmount = targetAmount;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public int getProgress() {
        return (int) ((currentAmount / targetAmount) * 100);
    }
}