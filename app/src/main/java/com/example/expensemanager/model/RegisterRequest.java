package com.example.expensemanager.model;

public class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String username;
    private String phone;

    public RegisterRequest(String fullName, String email, String password, String username, String phone) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
    }

    // Getters và Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}