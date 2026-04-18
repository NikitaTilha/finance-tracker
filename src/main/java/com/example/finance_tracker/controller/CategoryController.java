package com.example.finance_tracker.controller;

import com.example.finance_tracker.dto.category.CategoryResponse;
import com.example.finance_tracker.dto.category.CreateCategoryRequest;
import com.example.finance_tracker.dto.category.UpdateCategoryRequest;
import com.example.finance_tracker.service.CategoryService;
import com.example.finance_tracker.service.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CurrentUserService currentUserService;

    public CategoryController(CategoryService categoryService,
                              CurrentUserService currentUserService) {
        this.categoryService = categoryService;
        this.currentUserService = currentUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CreateCategoryRequest request,
                                           HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        return categoryService.createCategory(userId, request);
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories(HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        return categoryService.getAllByUserId(userId);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponse updateCategory(@PathVariable Long categoryId,
                                           @Valid @RequestBody UpdateCategoryRequest request,
                                           HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        return categoryService.updateCategory(userId, categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId,
                               HttpSession session) {
        Long userId = currentUserService.getCurrentUserId(session);
        categoryService.deleteCategory(userId, categoryId);
    }
}