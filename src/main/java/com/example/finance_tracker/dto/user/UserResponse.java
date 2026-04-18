package com.example.finance_tracker.dto.user;

import java.time.OffsetDateTime;

public class UserResponse {

    private Long id;
    private String username;
    private OffsetDateTime createdAt;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, OffsetDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}