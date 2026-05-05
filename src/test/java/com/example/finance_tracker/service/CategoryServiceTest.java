package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.category.CategoryResponse;
import com.example.finance_tracker.dto.category.CreateCategoryRequest;
import com.example.finance_tracker.entity.Category;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.exception.ApiException;
import com.example.finance_tracker.mapper.CategoryMapper;
import com.example.finance_tracker.repository.CategoryRepository;
import com.example.finance_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_shouldReturnResponse_whenDataIsValid() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("nikita");
        user.setPasswordHash("hash");
        user.setCreatedAt(OffsetDateTime.now());

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("  Еда  ");
        request.setType("expense");

        Category savedCategory = new Category();
        savedCategory.setId(10L);
        savedCategory.setUser(user);
        savedCategory.setName("Еда");
        savedCategory.setType("EXPENSE");
        savedCategory.setCreatedAt(OffsetDateTime.now());

        CategoryResponse mappedResponse = new CategoryResponse();
        mappedResponse.setId(10L);
        mappedResponse.setName("Еда");
        mappedResponse.setType("EXPENSE");
        mappedResponse.setCreatedAt(savedCategory.getCreatedAt());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByUser_IdAndNameAndType(userId, "Еда", "EXPENSE")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toResponse(savedCategory)).thenReturn(mappedResponse);

        CategoryResponse response = categoryService.createCategory(userId, request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Еда", response.getName());
        assertEquals("EXPENSE", response.getType());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void createCategory_shouldThrowException_whenCategoryAlreadyExists() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("nikita");
        user.setPasswordHash("hash");

        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName("Еда");
        request.setType("EXPENSE");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.existsByUser_IdAndNameAndType(userId, "Еда", "EXPENSE")).thenReturn(true);

        ApiException ex = assertThrows(
                ApiException.class,
                () -> categoryService.createCategory(userId, request)
        );

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        assertEquals("Категория уже существует", ex.getMessage());
    }

    @Test
    void getAllByUserId_shouldReturnCategoryList() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setUsername("nikita");
        user.setPasswordHash("hash");

        Category category1 = new Category();
        category1.setId(1L);
        category1.setUser(user);
        category1.setName("Еда");
        category1.setType("EXPENSE");
        category1.setCreatedAt(OffsetDateTime.now());

        Category category2 = new Category();
        category2.setId(2L);
        category2.setUser(user);
        category2.setName("Зарплата");
        category2.setType("INCOME");
        category2.setCreatedAt(OffsetDateTime.now());

        CategoryResponse response1 = new CategoryResponse();
        response1.setId(1L);
        response1.setName("Еда");
        response1.setType("EXPENSE");
        response1.setCreatedAt(category1.getCreatedAt());

        CategoryResponse response2 = new CategoryResponse();
        response2.setId(2L);
        response2.setName("Зарплата");
        response2.setType("INCOME");
        response2.setCreatedAt(category2.getCreatedAt());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findAllByUser_IdOrderByIdAsc(userId)).thenReturn(List.of(category1, category2));
        when(categoryMapper.toResponse(category1)).thenReturn(response1);
        when(categoryMapper.toResponse(category2)).thenReturn(response2);

        List<CategoryResponse> responses = categoryService.getAllByUserId(userId);

        assertEquals(2, responses.size());
        assertEquals("Еда", responses.get(0).getName());
        assertEquals("EXPENSE", responses.get(0).getType());
        assertEquals("Зарплата", responses.get(1).getName());
        assertEquals("INCOME", responses.get(1).getType());
    }
}