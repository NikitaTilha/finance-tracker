package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.entity.Category;
import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.repository.TransactionRepository;
import com.example.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyConversionService currencyConversionService;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void getBalance_shouldReturnResponse_whenCurrencyExists() {
        Long userId = 1L;

        Category category = new Category();
        category.setId(1L);
        category.setName("Зарплата");
        category.setType("INCOME");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCategory(category);
        transaction.setAmountCents(125000L);
        transaction.setCurrency("RUB");
        transaction.setOccurredAt(OffsetDateTime.now());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(currencyConversionService.normalizeCurrency("rub")).thenReturn("RUB");
        when(transactionRepository.findAllByUser_IdOrderByOccurredAtDescIdDesc(userId))
                .thenReturn(List.of(transaction));
        when(currencyConversionService.convertAmount(
                BigDecimal.valueOf(125000L, 2),
                "RUB",
                "RUB"
        )).thenReturn(BigDecimal.valueOf(125000L, 2));
        when(currencyConversionService.toMinorUnits(BigDecimal.valueOf(125000L, 2)))
                .thenReturn(125000L);

        BalanceResponse response = balanceService.getBalance(userId, "rub");

        assertNotNull(response);
        assertEquals("RUB", response.getCurrency());
        assertEquals(125000L, response.getAmountCents());
    }
}