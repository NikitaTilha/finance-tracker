package com.example.finance_tracker.dto.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class TransactionResponse {

    private Long id;
    private Long categoryId;
    private Long amountCents;
    private String currency;
    private OffsetDateTime occurredAt;
    private String note;
    private OffsetDateTime createdAt;
}