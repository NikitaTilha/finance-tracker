package com.example.finance_tracker.service;

import com.example.finance_tracker.exception.ConflictException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Service
public class CurrencyConversionService {

    private static final BigDecimal BYN_RATE = BigDecimal.ONE;
    private static final BigDecimal USD_TO_BYN = new BigDecimal("3.20");
    private static final BigDecimal RUB_TO_BYN = new BigDecimal("0.035");

    public String normalizeCurrency(String currency) {
        if (currency == null || currency.trim().isEmpty()) {
            throw new ConflictException("Валюта не указана");
        }

        String normalizedCurrency = currency.trim().toUpperCase(Locale.ROOT);

        if (!normalizedCurrency.equals("BYN")
                && !normalizedCurrency.equals("USD")
                && !normalizedCurrency.equals("RUB")) {
            throw new ConflictException("Неподдерживаемая валюта");
        }

        return normalizedCurrency;
    }

    public BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) {
        String normalizedFrom = normalizeCurrency(fromCurrency);
        String normalizedTo = normalizeCurrency(toCurrency);

        if (normalizedFrom.equals(normalizedTo)) {
            return amount;
        }

        BigDecimal amountInByn = amount.multiply(rateToByn(normalizedFrom));

        return amountInByn.divide(rateToByn(normalizedTo), 10, RoundingMode.HALF_UP);
    }

    public long toMinorUnits(BigDecimal amount) {
        return amount
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValue();
    }

    private BigDecimal rateToByn(String currency) {
        return switch (currency) {
            case "BYN" -> BYN_RATE;
            case "USD" -> USD_TO_BYN;
            case "RUB" -> RUB_TO_BYN;
            default -> throw new ConflictException("Неподдерживаемая валюта");
        };
    }
}