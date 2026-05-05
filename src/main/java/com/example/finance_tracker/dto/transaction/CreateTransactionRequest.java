package com.example.finance_tracker.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateTransactionRequest {

    private Long categoryId;

    @NotNull
    private Long amountCents;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @NotNull
    private OffsetDateTime occurredAt;

    @Size(max = 500)
    private String note;
}