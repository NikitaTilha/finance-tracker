package com.example.finance_tracker.dto.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String type;
    private OffsetDateTime createdAt;
}