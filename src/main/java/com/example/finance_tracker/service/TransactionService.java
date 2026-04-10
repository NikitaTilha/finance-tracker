package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.dto.transaction.CreateTransactionRequest;
import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.dto.transaction.UpdateTransactionRequest;
import com.example.finance_tracker.entity.Category;
import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.mapper.TransactionMapper;
import com.example.finance_tracker.repository.CategoryRepository;
import com.example.finance_tracker.repository.TransactionRepository;
import com.example.finance_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public TransactionResponse createTransaction(Long userId, CreateTransactionRequest request) {
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrNull(userId, request.getCategoryId());

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmountCents(request.getAmountCents());
        transaction.setCurrency(normalizeCurrency(request.getCurrency()));
        transaction.setOccurredAt(request.getOccurredAt());
        transaction.setNote(normalizeNote(request.getNote()));

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionMapper.toResponse(savedTransaction);
    }

    public List<TransactionResponse> getAllByUserId(Long userId) {
        getUserOrThrow(userId);

        return transactionRepository.findAllByUser_IdOrderByOccurredAtDescIdDesc(userId)
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, UpdateTransactionRequest request) {
        getUserOrThrow(userId);

        Transaction transaction = transactionRepository.findByIdAndUser_Id(transactionId, userId)
                .orElseThrow(() -> new NoSuchElementException("Транзакция не найдена"));

        Category category = getCategoryOrNull(userId, request.getCategoryId());

        transaction.setCategory(category);
        transaction.setAmountCents(request.getAmountCents());
        transaction.setCurrency(normalizeCurrency(request.getCurrency()));
        transaction.setOccurredAt(request.getOccurredAt());
        transaction.setNote(normalizeNote(request.getNote()));

        return TransactionMapper.toResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        getUserOrThrow(userId);

        Transaction transaction = transactionRepository.findByIdAndUser_Id(transactionId, userId)
                .orElseThrow(() -> new NoSuchElementException("Транзакция не найдена"));

        transactionRepository.delete(transaction);
    }

    public BalanceResponse getBalance(Long userId, String currency) {
        getUserOrThrow(userId);

        String normalizedCurrency = normalizeCurrency(currency);
        Long amountCents = transactionRepository.sumAmountCentsByUserIdAndCurrency(userId, normalizedCurrency);

        return new BalanceResponse(normalizedCurrency, amountCents);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
    }

    private Category getCategoryOrNull(Long userId, Long categoryId) {
        if (categoryId == null) {
            return null;
        }

        return categoryRepository.findByIdAndUser_Id(categoryId, userId)
                .orElseThrow(() -> new NoSuchElementException("Категория не найдена"));
    }

    private String normalizeCurrency(String currency) {
        return currency.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeNote(String note) {
        if (note == null) {
            return null;
        }

        String trimmed = note.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}