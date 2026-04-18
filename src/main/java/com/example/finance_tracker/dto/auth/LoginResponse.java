package com.example.finance_tracker.dto.auth;

import java.time.OffsetDateTime;

public class LoginResponse {

    private Long id;
    private String username;
    private OffsetDateTime createdAt;
    private boolean authenticated;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String username, OffsetDateTime createdAt, boolean authenticated) {
        this.id = id;
        this.username = username;
        this.createdAt = createdAt;
        this.authenticated = authenticated;
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

    public boolean isAuthenticated() {
        return authenticated;
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

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}