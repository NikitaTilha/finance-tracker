package com.example.finance_tracker.mapper;

import com.example.finance_tracker.dto.category.CategoryResponse;
import com.example.finance_tracker.entity.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setType(category.getType());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
}