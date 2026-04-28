package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.dto.transaction.CreateTransactionRequest;
import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.dto.transaction.UpdateTransactionRequest;
import com.example.finance_tracker.entity.Category;
import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.exception.ConflictException;
import com.example.finance_tracker.mapper.TransactionMapper;
import com.example.finance_tracker.repository.CategoryRepository;
import com.example.finance_tracker.repository.TransactionRepository;
import com.example.finance_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private static final BigDecimal BYN_RATE = BigDecimal.ONE;
    private static final BigDecimal USD_TO_BYN = new BigDecimal("3.20");
    private static final BigDecimal RUB_TO_BYN = new BigDecimal("0.035");

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

        String normalizedCurrency = normalizeCurrency(request.getCurrency());
        validateBalanceAfterCreate(userId, category, normalizedCurrency, request.getAmountCents());

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmountCents(request.getAmountCents());
        transaction.setCurrency(normalizedCurrency);
        transaction.setOccurredAt(request.getOccurredAt());
        transaction.setNote(normalizeNote(request.getNote()));

        Transaction savedTransaction = transactionRepository.save(transaction);
        return TransactionMapper.toResponse(savedTransaction);
    }

    public List<TransactionResponse> getAllByFilters(
            Long userId,
            Long categoryId,
            String currency,
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo
    ) {
        getUserOrThrow(userId);

        if (categoryId != null) {
            categoryRepository.findByIdAndUser_Id(categoryId, userId)
                    .orElseThrow(() -> new NoSuchElementException("Категория не найдена"));
        }

        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalStateException("dateFrom не может быть позже dateTo");
        }

        if (categoryId == null && currency == null && dateFrom == null && dateTo == null) {
            return transactionRepository.findAllByUser_IdOrderByOccurredAtDescIdDesc(userId)
                    .stream()
                    .map(TransactionMapper::toResponse)
                    .toList();
        }

        String normalizedCurrency = currency == null ? null : normalizeCurrency(currency);

        return transactionRepository.findAllByFilters(
                        userId,
                        categoryId,
                        normalizedCurrency,
                        dateFrom,
                        dateTo
                )
                .stream()
                .map(TransactionMapper::toResponse)
                .toList();
    }

    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, UpdateTransactionRequest request) {
        getUserOrThrow(userId);

        Transaction transaction = transactionRepository.findByIdAndUser_Id(transactionId, userId)
                .orElseThrow(() -> new NoSuchElementException("Транзакция не найдена"));

        Category newCategory = getCategoryOrNull(userId, request.getCategoryId());
        String normalizedCurrency = normalizeCurrency(request.getCurrency());

        validateBalanceAfterUpdate(userId, transaction, newCategory, normalizedCurrency, request.getAmountCents());

        transaction.setCategory(newCategory);
        transaction.setAmountCents(request.getAmountCents());
        transaction.setCurrency(normalizedCurrency);
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
        List<Transaction> transactions = transactionRepository.findAllByUser_IdOrderByOccurredAtDescIdDesc(userId);

        BigDecimal balance = sumTransactionsInCurrency(transactions, normalizedCurrency);

        return new BalanceResponse(normalizedCurrency, toMinorUnits(balance));
    }

    private void validateBalanceAfterCreate(
            Long userId,
            Category category,
            String currency,
            Long amountCents
    ) {
        List<Transaction> transactions = transactionRepository.findAllByUser_IdOrderByOccurredAtDescIdDesc(userId);

        BigDecimal currentBalance = sumTransactionsInCurrency(transactions, currency);
        BigDecimal newSignedAmount = toSignedMajorAmount(category, amountCents);
        BigDecimal newBalance = currentBalance.add(newSignedAmount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ConflictException("Недостаточно средств");
        }
    }

    private void validateBalanceAfterUpdate(
            Long userId,
            Transaction oldTransaction,
            Category newCategory,
            String newCurrency,
            Long newAmountCents
    ) {
        List<Transaction> transactions = transactionRepository.findAllByUser_IdOrderByOccurredAtDescIdDesc(userId);

        BigDecimal currentBalance = sumTransactionsInCurrency(transactions, newCurrency);
        BigDecimal oldSignedAmountConverted = convertSignedTransactionAmount(oldTransaction, newCurrency);
        BigDecimal newSignedAmount = toSignedMajorAmount(newCategory, newAmountCents);

        BigDecimal newBalance = currentBalance
                .subtract(oldSignedAmountConverted)
                .add(newSignedAmount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new ConflictException("Недостаточно средств");
        }
    }

    private BigDecimal sumTransactionsInCurrency(List<Transaction> transactions, String targetCurrency) {
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            total = total.add(convertSignedTransactionAmount(transaction, targetCurrency));
        }

        return total;
    }

    private BigDecimal convertSignedTransactionAmount(Transaction transaction, String targetCurrency) {
        BigDecimal majorAmount = BigDecimal.valueOf(transaction.getAmountCents(), 2);

        if (isExpense(transaction.getCategory())) {
            majorAmount = majorAmount.negate();
        }

        return convertAmount(majorAmount, transaction.getCurrency(), targetCurrency);
    }

    private BigDecimal toSignedMajorAmount(Category category, Long amountCents) {
        BigDecimal amount = BigDecimal.valueOf(amountCents, 2);

        if (isExpense(category)) {
            return amount.negate();
        }

        return amount;
    }

    private boolean isExpense(Category category) {
        return category != null && "EXPENSE".equals(String.valueOf(category.getType()));
    }

    private BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        String normalizedFrom = normalizeCurrency(fromCurrency);
        String normalizedTo = normalizeCurrency(toCurrency);

        if (normalizedFrom.equals(normalizedTo)) {
            return amount;
        }

        BigDecimal amountInByn = amount.multiply(rateToByn(normalizedFrom));

        return amountInByn.divide(rateToByn(normalizedTo), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal rateToByn(String currency) {
        return switch (currency) {
            case "BYN" -> BYN_RATE;
            case "USD" -> USD_TO_BYN;
            case "RUB" -> RUB_TO_BYN;
            default -> throw new ConflictException("Неподдерживаемая валюта");
        };
    }

    private long toMinorUnits(BigDecimal amount) {
        return amount
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValue();
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
        if (currency == null || currency.trim().isEmpty()) {
            throw new ConflictException("Валюта не указана");
        }

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