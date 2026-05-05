package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.transaction.CreateTransactionRequest;
import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.dto.transaction.UpdateTransactionRequest;

import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(Long userId, CreateTransactionRequest request);

    List<TransactionResponse> getAllByFilters(
            Long userId,
            Long categoryId,
            String currency,
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo
    );

    TransactionResponse updateTransaction(Long userId, Long transactionId, UpdateTransactionRequest request);

    void deleteTransaction(Long userId, Long transactionId);
}