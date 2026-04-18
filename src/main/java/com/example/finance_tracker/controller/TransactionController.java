package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.dto.transaction.CreateTransactionRequest;
import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.dto.transaction.UpdateTransactionRequest;
import com.example.finance_tracker.service.CurrentUserService;
import com.example.finance_tracker.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrentUserService currentUserService;

    public TransactionController(TransactionService transactionService,
                                 CurrentUserService currentUserService) {
        this.transactionService = transactionService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request,
                                                 HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        return transactionService.createTransaction(userId, request);
    }

    @GetMapping
    public List<TransactionResponse> getAllTransactions(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) OffsetDateTime dateFrom,
            @RequestParam(required = false) OffsetDateTime dateTo,
            HttpSession session
    ) {
        Long userId = currentUserService.getCurrentUserId(session);
        return transactionService.getAllByFilters(userId, categoryId, currency, dateFrom, dateTo);
    }

    @PutMapping("/{transactionId}")
    public TransactionResponse updateTransaction(@PathVariable Long transactionId,
                                                 @Valid @RequestBody UpdateTransactionRequest request,
                                                 HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        return transactionService.updateTransaction(userId, transactionId, request);
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long transactionId,
                                  HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        transactionService.deleteTransaction(userId, transactionId);
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance(@RequestParam String currency,
                                      HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        return transactionService.getBalance(userId, currency);
    }
}