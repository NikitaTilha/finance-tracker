package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.entity.Category;
import com.example.finance_tracker.entity.Transaction;

public interface BalanceService {

    BalanceResponse getBalance(Long userId, String currency);

    void validateBalanceAfterCreate(
            Long userId,
            Category category,
            String currency,
            Long amountCents
    );

    void validateBalanceAfterUpdate(
            Long userId,
            Transaction oldTransaction,
            Category newCategory,
            String newCurrency,
            Long newAmountCents
    );
}