package com.example.expensemanager.model;

import java.io.Serializable;
import java.util.Date;

public class Goal implements Serializable {
    private String title;
    private double currentAmount;
    private double targetAmount;
    private String contributionType; // New field
    private Date deadline;         // New field

    public Goal(String title, double currentAmount, double targetAmount, String contributionType, Date deadline) {
        this.title = title;
        this.currentAmount = currentAmount;
        this.targetAmount = targetAmount;
        this.contributionType = contributionType;
        this.deadline = deadline;
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

    public String getContributionType() {
        return contributionType;
    }

    public Date getDeadline() {
        return deadline;
    }

    public int getProgress() {
        return (int) ((currentAmount / targetAmount) * 100);
    }
}