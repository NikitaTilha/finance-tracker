package com.example.finance_tracker.service;

public interface SessionService {

    void saveCurrentUser(Long userId, String username);

    void invalidateCurrentSession();
}