package com.example.finance_tracker.dto.balance;

public class BalanceResponse {

    private String currency;
    private Long amountCents;

    public BalanceResponse() {
    }

    public BalanceResponse(String currency, Long amountCents) {
        this.currency = currency;
        this.amountCents = amountCents;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getAmountCents() {
        return amountCents;
    }

    public void setAmountCents(Long amountCents) {
        this.amountCents = amountCents;
    }
}