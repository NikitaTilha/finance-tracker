package com.example.finance_tracker.mapper;

import com.example.finance_tracker.dto.category.CategoryResponse;
import com.example.finance_tracker.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);
}