package com.ruchikart.model;

import java.time.LocalDateTime;

public class User {
    private int userID;
    private String username;
    private String password;
    private String email;
    private String address;
    private String role;
    private LocalDateTime createdDate;
    private LocalDateTime lastLoginDate;

    public User() {}

    public User(int userID, String username, String password, String email,
                String address, String role, LocalDateTime createdDate, LocalDateTime lastLoginDate) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.role = role;
        this.createdDate = createdDate;
        this.lastLoginDate = lastLoginDate;
    }

    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(LocalDateTime lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    @Override
    public String toString() {
        return "User{userID=" + userID + ", username='" + username + "', role='" + role + "'}";
    }
}
