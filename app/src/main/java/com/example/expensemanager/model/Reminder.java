package com.example.expensemanager.model;

import java.io.Serializable;
import java.util.Date;

public class Reminder implements Serializable {
    private String title;
    private double amount;
    private Date dueDate;
    private String frequency; // New field for frequency
    private boolean state;   // New field for state (e.g., active/inactive)

    public Reminder(String title, double amount, Date dueDate, String frequency, boolean state) {
        this.title = title;
        this.amount = amount;
        this.dueDate = dueDate;
        this.frequency = frequency;
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

}