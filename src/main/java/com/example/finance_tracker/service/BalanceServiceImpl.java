package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.entity.Category;
import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.exception.ConflictException;
import com.example.finance_tracker.repository.TransactionRepository;
import com.example.finance_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BalanceServiceImpl implements BalanceService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CurrencyConversionService currencyConversionService;

    @Override
    public BalanceResponse getBalance(Long userId, String currency) {
        validateUserExists(userId);

        String normalizedCurrency = currencyConversionService.normalizeCurrency(currency);
        List<Transaction> transactions = transactionRepository.findAllByUser_IdOrderByOccurredAtDescIdDesc(userId);

        BigDecimal balance = sumTransactionsInCurrency(transactions, normalizedCurrency);

        return new BalanceResponse(
                normalizedCurrency,
                currencyConversionService.toMinorUnits(balance)
        );
    }

    @Override
    public void validateBalanceAfterCreate(
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

    @Override
    public void validateBalanceAfterUpdate(
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

        return currencyConversionService.convertAmount(
                majorAmount,
                transaction.getCurrency(),
                targetCurrency
        );
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

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("Пользователь не найден");
        }
    }
}