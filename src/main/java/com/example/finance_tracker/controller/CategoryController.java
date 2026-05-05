package com.example.finance_tracker.controller;

import com.example.finance_tracker.config.CurrentUserId;
import com.example.finance_tracker.dto.category.CategoryResponse;
import com.example.finance_tracker.dto.category.CreateCategoryRequest;
import com.example.finance_tracker.dto.category.UpdateCategoryRequest;
import com.example.finance_tracker.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @CurrentUserId Long userId,
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        return categoryService.createCategory(userId, request);
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories(
            @CurrentUserId Long userId
    ) {
        return categoryService.getAllByUserId(userId);
    }

    @PutMapping("/{categoryId}")
    public CategoryResponse updateCategory(
            @CurrentUserId Long userId,
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        return categoryService.updateCategory(userId, categoryId, request);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @CurrentUserId Long userId,
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(userId, categoryId);
    }
}