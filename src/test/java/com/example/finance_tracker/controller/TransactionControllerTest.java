package com.example.finance_tracker.controller;

import com.example.finance_tracker.config.CurrentUserIdArgumentResolver;
import com.example.finance_tracker.config.WebConfig;
import com.example.finance_tracker.dto.balance.BalanceResponse;
import com.example.finance_tracker.dto.transaction.CreateTransactionRequest;
import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.exception.GlobalExceptionHandler;
import com.example.finance_tracker.service.BalanceService;
import com.example.finance_tracker.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import({
        GlobalExceptionHandler.class,
        WebConfig.class,
        CurrentUserIdArgumentResolver.class
})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private BalanceService balanceService;

    @Test
    void getAllTransactions_shouldReturn200_whenUserIsAuthenticated() throws Exception {
        TransactionResponse transaction = new TransactionResponse();
        transaction.setId(1L);
        transaction.setCategoryId(1L);
        transaction.setAmountCents(-25000L);
        transaction.setCurrency("RUB");
        transaction.setOccurredAt(OffsetDateTime.now());
        transaction.setNote("Продукты");
        transaction.setCreatedAt(OffsetDateTime.now());

        when(transactionService.getAllByFilters(1L, null, null, null, null))
                .thenReturn(List.of(transaction));

        mockMvc.perform(get("/api/transactions")
                        .sessionAttr("userId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].categoryId").value(1))
                .andExpect(jsonPath("$[0].amountCents").value(-25000))
                .andExpect(jsonPath("$[0].currency").value("RUB"))
                .andExpect(jsonPath("$[0].note").value("Продукты"));
    }

    @Test
    void getAllTransactions_shouldReturn401_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Пользователь не авторизован"));
    }

    @Test
    void createTransaction_shouldReturn201_whenDataIsValid() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setCategoryId(1L);
        request.setAmountCents(-25000L);
        request.setCurrency("RUB");
        request.setOccurredAt(OffsetDateTime.now());
        request.setNote("Продукты");

        TransactionResponse response = new TransactionResponse();
        response.setId(1L);
        response.setCategoryId(1L);
        response.setAmountCents(-25000L);
        response.setCurrency("RUB");
        response.setOccurredAt(request.getOccurredAt());
        response.setNote("Продукты");
        response.setCreatedAt(OffsetDateTime.now());

        when(transactionService.createTransaction(eq(1L), any(CreateTransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.amountCents").value(-25000))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.note").value("Продукты"));
    }

    @Test
    void createTransaction_shouldReturn400_whenRequestIsInvalid() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setCategoryId(null);
        request.setAmountCents(null);
        request.setCurrency("");
        request.setOccurredAt(null);
        request.setNote("Продукты");

        mockMvc.perform(post("/api/transactions")
                        .sessionAttr("userId", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации"));
    }

    @Test
    void getBalance_shouldReturn200_whenUserIsAuthenticated() throws Exception {
        BalanceResponse response = new BalanceResponse();
        response.setCurrency("RUB");
        response.setAmountCents(-50000L);

        when(balanceService.getBalance(1L, "RUB")).thenReturn(response);

        mockMvc.perform(get("/api/transactions/balance")
                        .sessionAttr("userId", 1L)
                        .param("currency", "RUB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.amountCents").value(-50000));
    }

    @Test
    void getBalance_shouldReturn401_whenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/transactions/balance")
                        .param("currency", "RUB"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Пользователь не авторизован"));
    }
}