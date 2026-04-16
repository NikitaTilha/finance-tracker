package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.dto.transaction.CreateTransactionRequest;
import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.dto.transaction.UpdateTransactionRequest;
import com.example.finance_tracker.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/users/{userId}/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(
            @PathVariable Long userId,
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        return transactionService.createTransaction(userId, request);
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @Size(min = 3, max = 3) String currency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo
    ) {
        return transactionService.getAllByFilters(userId, categoryId, currency, dateFrom, dateTo);
    }

    @PutMapping("/{transactionId}")
    public TransactionResponse updateTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId,
            @Valid @RequestBody UpdateTransactionRequest request
    ) {
        return transactionService.updateTransaction(userId, transactionId, request);
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId
    ) {
        transactionService.deleteTransaction(userId, transactionId);
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(
            @PathVariable Long userId,
            @RequestParam @NotBlank @Size(min = 3, max = 3) String currency
    ) {
        return transactionService.getBalance(userId, currency);
    }
}