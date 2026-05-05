package com.example.finance_tracker.controller;

import com.example.finance_tracker.config.CurrentUserId;
import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.dto.transaction.CreateTransactionRequest;
import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.dto.transaction.UpdateTransactionRequest;
import com.example.finance_tracker.service.BalanceService;
import com.example.finance_tracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final BalanceService balanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @CurrentUserId Long userId,
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        return transactionService.createTransaction(userId, request);
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) OffsetDateTime dateFrom,
            @RequestParam(required = false) OffsetDateTime dateTo
    ) {
        return transactionService.getAllByFilters(userId, categoryId, currency, dateFrom, dateTo);
    }

    @PutMapping("/{transactionId}")
    public TransactionResponse updateTransaction(
            @CurrentUserId Long userId,
            @PathVariable Long transactionId,
            @Valid @RequestBody UpdateTransactionRequest request
    ) {
        return transactionService.updateTransaction(userId, transactionId, request);
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(
            @CurrentUserId Long userId,
            @PathVariable Long transactionId
    ) {
        transactionService.deleteTransaction(userId, transactionId);
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(
            @CurrentUserId Long userId,
            @RequestParam String currency
    ) {
        return balanceService.getBalance(userId, currency);
    }
}