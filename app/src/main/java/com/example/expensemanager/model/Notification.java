package com.example.expensemanager.model;

public class Notification {
    private int iconResId;
    private String title;
    private String description;
    private String timestamp;

    public Notification(int iconResId, String title, String description, String timestamp) {
        this.iconResId = iconResId;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
