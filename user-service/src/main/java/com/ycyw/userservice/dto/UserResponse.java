package com.ycyw.userservice.dto;

import com.ycyw.userservice.model.UserRole;

public class UserResponse {

    private String id;
    private String username;
    private UserRole role;
    private boolean active;

    public UserResponse() {
    }

    public UserResponse(String id, String username, UserRole role, boolean active) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
