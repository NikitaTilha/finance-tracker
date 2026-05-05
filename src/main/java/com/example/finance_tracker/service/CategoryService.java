package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.category.CategoryResponse;
import com.example.finance_tracker.dto.category.CreateCategoryRequest;
import com.example.finance_tracker.dto.category.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(Long userId, CreateCategoryRequest request);

    List<CategoryResponse> getAllByUserId(Long userId);

    CategoryResponse updateCategory(Long userId, Long categoryId, UpdateCategoryRequest request);

    void deleteCategory(Long userId, Long categoryId);
}