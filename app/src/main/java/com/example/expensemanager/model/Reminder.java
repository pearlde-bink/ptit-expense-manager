package com.example.expensemanager.model;

import java.io.Serializable;

public class Reminder implements Serializable {
    private String startDate, endDate;
    private String title;
    private Double amount;
    private boolean state;

    public Reminder(String startReminder, String title, boolean state, Double amount, String endReminder) {
        this.startDate = startReminder;
        this.endDate = endReminder;
        this.title = title;
        this.amount = amount;
        this.state = state;
    }

    public Reminder() {
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startReminder) {
        this.startDate = startReminder;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endReminder) {
        this.endDate = endReminder;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
