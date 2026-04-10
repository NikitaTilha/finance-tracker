package com.example.finance_tracker.repository;

import com.example.finance_tracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByUser_IdAndNameAndType(Long userId, String name, String type);

    boolean existsByUser_IdAndNameAndTypeAndIdNot(Long userId, String name, String type, Long id);

    List<Category> findAllByUser_IdOrderByIdAsc(Long userId);

    Optional<Category> findByIdAndUser_Id(Long id, Long userId);
}