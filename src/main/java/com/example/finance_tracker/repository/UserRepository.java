package com.example.finance_tracker.repository;

import com.example.finance_tracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}