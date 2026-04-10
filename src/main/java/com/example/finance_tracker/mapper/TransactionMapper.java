package com.example.finance_tracker.mapper;

import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.entity.Transaction;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setCategoryId(
                transaction.getCategory() != null ? transaction.getCategory().getId() : null
        );
        response.setAmountCents(transaction.getAmountCents());
        response.setCurrency(transaction.getCurrency());
        response.setOccurredAt(transaction.getOccurredAt());
        response.setNote(transaction.getNote());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }
}