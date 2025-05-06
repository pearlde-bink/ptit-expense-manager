package com.example.expensemanager.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Goal implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("title")
    private String title;

    @SerializedName("currentAmount")
    private double currentAmount;

    @SerializedName("targetAmount")
    private double targetAmount;

    @SerializedName("contributionType")
    private String contributionType;

    @SerializedName("deadline")
    private String deadline; // ISO format string (e.g., "2025-12-31")

    // Constructor for creating a new goal
    public Goal(int userId, String title, double currentAmount, double targetAmount, String contributionType, String deadline) {
        this.id = -1; // -1 indicates a new goal to be inserted
        this.userId = userId;
        this.title = title;
        this.currentAmount = currentAmount;
        this.targetAmount = targetAmount;
        this.contributionType = contributionType;
        this.deadline = deadline;
    }

    // Default constructor for Gson
    public Goal() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getContributionType() {
        return contributionType;
    }

    public void setContributionType(String contributionType) {
        this.contributionType = contributionType;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getProgress() {
        return (int) ((currentAmount / targetAmount) * 100);
    }
}