package com.example.finance_tracker.service;

import java.math.BigDecimal;

public interface CurrencyConversionService {

    String normalizeCurrency(String currency);

    BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency);

    long toMinorUnits(BigDecimal amount);
}