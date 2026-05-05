package com.example.finance_tracker.service;

import com.example.finance_tracker.dto.category.CategoryResponse;
import com.example.finance_tracker.dto.category.CreateCategoryRequest;
import com.example.finance_tracker.dto.category.UpdateCategoryRequest;
import com.example.finance_tracker.entity.Category;
import com.example.finance_tracker.entity.User;
import com.example.finance_tracker.exception.ConflictException;
import com.example.finance_tracker.mapper.CategoryMapper;
import com.example.finance_tracker.repository.CategoryRepository;
import com.example.finance_tracker.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               UserRepository userRepository,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(Long userId, CreateCategoryRequest request) {
        User user = getUserOrThrow(userId);

        String normalizedName = normalizeName(request.getName());
        String normalizedType = normalizeType(request.getType());

        boolean exists = categoryRepository.existsByUser_IdAndNameAndType(
                userId,
                normalizedName,
                normalizedType
        );

        if (exists) {
            throw new ConflictException("Категория уже существует");
        }

        Category category = new Category();
        category.setUser(user);
        category.setName(normalizedName);
        category.setType(normalizedType);

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    public List<CategoryResponse> getAllByUserId(Long userId) {
        getUserOrThrow(userId);

        return categoryRepository.findAllByUser_IdOrderByIdAsc(userId)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long userId, Long categoryId, UpdateCategoryRequest request) {
        getUserOrThrow(userId);

        Category category = categoryRepository.findByIdAndUser_Id(categoryId, userId)
                .orElseThrow(() -> new NoSuchElementException("Категория не найдена"));

        String normalizedName = normalizeName(request.getName());
        String normalizedType = normalizeType(request.getType());

        boolean exists = categoryRepository.existsByUser_IdAndNameAndTypeAndIdNot(
                userId,
                normalizedName,
                normalizedType,
                categoryId
        );

        if (exists) {
            throw new ConflictException("Категория уже существует");
        }

        category.setName(normalizedName);
        category.setType(normalizedType);

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        getUserOrThrow(userId);

        Category category = categoryRepository.findByIdAndUser_Id(categoryId, userId)
                .orElseThrow(() -> new NoSuchElementException("Категория не найдена"));

        categoryRepository.delete(category);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
    }

    private String normalizeName(String name) {
        return name.trim();
    }

    private String normalizeType(String type) {
        return type.trim().toUpperCase(Locale.ROOT);
    }
}