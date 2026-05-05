package com.example.finance_tracker.mapper;

import com.example.finance_tracker.dto.transaction.TransactionResponse;
import com.example.finance_tracker.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "categoryId", source = "category.id")
    TransactionResponse toResponse(Transaction transaction);
}