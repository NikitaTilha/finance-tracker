package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.category.CategoryResponse;
import com.example.finance_tracker.dto.category.CreateCategoryRequest;
import com.example.finance_tracker.exception.GlobalExceptionHandler;
import com.example.finance_tracker.exception.UnauthorizedException;
import com.example.finance_tracker.service.CategoryService;
import com.example.finance_tracker.service.CurrentUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import(GlobalExceptionHandler.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CurrentUserService currentUserService;

    @Test
    void getAllCategories_shouldReturn200_whenUserIsAuthenticated() throws Exception {
        CategoryResponse category1 = new CategoryResponse();
        category1.setId(1L);
        category1.setName("Еда");
        category1.setType("EXPENSE");
        category1.setCreatedAt(OffsetDateTime.now());

        CategoryResponse category2 = new CategoryResponse();
        category2.setId(2L);
        category2.setName("Зарплата");
        category2.setType("INCOME");
        category2.setCreatedAt(OffsetDateTime.now());

        when(currentUserService.getCurrentUserId(any(HttpSession.class))).thenReturn(1L);
        when(categoryService.getAllByUserId(1L)).thenReturn(List.of(category1, category2));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Еда"))
                .andExpect(jsonPath("$[0].type").value("EXPENSE"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Зарплата"))
                .andExpect(jsonPath("$[1].type").value("INCOME"));
    }

    @Test
    void getAllCategories_shouldReturn401_whenUserIsNotAuthenticated() throws Exception {
        when(currentUserService.getCurrentUserId(any(HttpSession.class)))
                .thenThrow(new UnauthorizedException("Пользователь не авторизован"));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Пользователь не авторизован"));
    }

    @Test
    void createCategory_shouldReturn201_whenDataIsValid() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Еда");
        request.setType("EXPENSE");

        CategoryResponse response = new CategoryResponse();
        response.setId(1L);
        response.setName("Еда");
        response.setType("EXPENSE");
        response.setCreatedAt(OffsetDateTime.now());

        when(currentUserService.getCurrentUserId(any(HttpSession.class))).thenReturn(1L);
        when(categoryService.createCategory(any(Long.class), any(CreateCategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Еда"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    void createCategory_shouldReturn409_whenCategoryAlreadyExists() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Еда");
        request.setType("EXPENSE");

        when(currentUserService.getCurrentUserId(any(HttpSession.class))).thenReturn(1L);
        when(categoryService.createCategory(any(Long.class), any(CreateCategoryRequest.class)))
                .thenThrow(new IllegalStateException("Категория уже существует"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Категория уже существует"));
    }

    @Test
    void createCategory_shouldReturn400_whenRequestIsInvalid() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("");
        request.setType("");

        when(currentUserService.getCurrentUserId(any(HttpSession.class))).thenReturn(1L);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации"));
    }
}