package com.example.expensemanager.model;

//import jakarta.persistence.*;
import java.time.LocalDateTime;

public class Category {
    private Long id;

    private String title;

    private String type;

    private String icon;

    private LocalDateTime createdAt;

//    protected void onCreate() {
//        this.createdAt = LocalDateTime.now();
//    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
