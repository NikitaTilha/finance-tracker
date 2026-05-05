package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.transaction.CreateTransactionRequest;
import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.entity.Category;
import com.example.finance_tracker.entity.Transaction;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.mapper.TransactionMapper;
import com.example.finance_tracker.repository.CategoryRepository;
import com.example.finance_tracker.repository.TransactionRepository;
import com.example.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BalanceService balanceService;

    @Mock
    private CurrencyConversionService currencyConversionService;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void createTransaction_shouldReturnResponse_whenDataIsValid() {
        Long userId = 1L;
        Long categoryId = 10L;
        OffsetDateTime occurredAt = OffsetDateTime.now();
        OffsetDateTime createdAt = OffsetDateTime.now();

        User user = new User();
        user.setId(userId);
        user.setUsername("nikita");
        user.setPasswordHash("hash");
        user.setCreatedAt(createdAt);

        Category category = new Category();
        category.setId(categoryId);
        category.setUser(user);
        category.setName("Еда");
        category.setType("EXPENSE");
        category.setCreatedAt(createdAt);

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setCategoryId(categoryId);
        request.setAmountCents(-25000L);
        request.setCurrency("rub");
        request.setOccurredAt(occurredAt);
        request.setNote("  Продукты  ");

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(100L);
        savedTransaction.setUser(user);
        savedTransaction.setCategory(category);
        savedTransaction.setAmountCents(-25000L);
        savedTransaction.setCurrency("RUB");
        savedTransaction.setOccurredAt(occurredAt);
        savedTransaction.setNote("Продукты");
        savedTransaction.setCreatedAt(createdAt);

        TransactionResponse mappedResponse = new TransactionResponse();
        mappedResponse.setId(100L);
        mappedResponse.setCategoryId(categoryId);
        mappedResponse.setAmountCents(-25000L);
        mappedResponse.setCurrency("RUB");
        mappedResponse.setOccurredAt(occurredAt);
        mappedResponse.setNote("Продукты");
        mappedResponse.setCreatedAt(createdAt);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId)).thenReturn(Optional.of(category));
        when(currencyConversionService.normalizeCurrency("rub")).thenReturn("RUB");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
        when(transactionMapper.toResponse(savedTransaction)).thenReturn(mappedResponse);

        TransactionResponse response = transactionService.createTransaction(userId, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(categoryId, response.getCategoryId());
        assertEquals(-25000L, response.getAmountCents());
        assertEquals("RUB", response.getCurrency());
        assertEquals("Продукты", response.getNote());
        assertEquals(occurredAt, response.getOccurredAt());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void createTransaction_shouldThrowException_whenCategoryNotFound() {
        Long userId = 1L;
        Long categoryId = 10L;

        User user = new User();
        user.setId(userId);
        user.setUsername("nikita");
        user.setPasswordHash("hash");

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setCategoryId(categoryId);
        request.setAmountCents(-25000L);
        request.setCurrency("RUB");
        request.setOccurredAt(OffsetDateTime.now());
        request.setNote("Продукты");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser_Id(categoryId, userId)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> transactionService.createTransaction(userId, request)
        );

        assertEquals("Категория не найдена", ex.getMessage());
    }

    @Test
    void getAllByFilters_shouldReturnTransactions_whenFiltersAreEmpty() {
        Long userId = 1L;
        OffsetDateTime now = OffsetDateTime.now();

        User user = new User();
        user.setId(userId);
        user.setUsername("nikita");
        user.setPasswordHash("hash");

        Category category = new Category();
        category.setId(1L);
        category.setUser(user);
        category.setName("Еда");
        category.setType("EXPENSE");

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmountCents(-25000L);
        transaction.setCurrency("RUB");
        transaction.setOccurredAt(now);
        transaction.setNote("Продукты");
        transaction.setCreatedAt(now);

        TransactionResponse mappedResponse = new TransactionResponse();
        mappedResponse.setId(1L);
        mappedResponse.setCategoryId(1L);
        mappedResponse.setAmountCents(-25000L);
        mappedResponse.setCurrency("RUB");
        mappedResponse.setOccurredAt(now);
        mappedResponse.setNote("Продукты");
        mappedResponse.setCreatedAt(now);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.findAllByUser_IdOrderByOccurredAtDescIdDesc(userId))
                .thenReturn(List.of(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(mappedResponse);

        List<TransactionResponse> responses = transactionService.getAllByFilters(
                userId,
                null,
                null,
                null,
                null
        );

        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(1L, responses.get(0).getCategoryId());
        assertEquals(-25000L, responses.get(0).getAmountCents());
        assertEquals("RUB", responses.get(0).getCurrency());
    }
}