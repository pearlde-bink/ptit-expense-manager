package com.example.expensemanager.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BudgetOverview implements Serializable {
    @SerializedName("budgetedAmount")
    private double budgetedAmount;

    @SerializedName("totalIncome")
    private double totalIncome;

    @SerializedName("totalExpense")
    private double totalExpense;

    @SerializedName("currentAmount")
    private double currentAmount;

    // Constructors, getters, and setters
    public BudgetOverview() {}

    public double getBudgetedAmount() {
        return budgetedAmount;
    }

    public void setBudgetedAmount(double budgetedAmount) {
        this.budgetedAmount = budgetedAmount;
    }

    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }
    public double getTotalExpense() { return totalExpense; }
    public void setTotalExpense(double totalExpense) { this.totalExpense = totalExpense; }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }
}