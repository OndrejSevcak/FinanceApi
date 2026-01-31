package com.example.demo.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FxService {

    // In real implementation this would call an external/internal FX service.
    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null) return BigDecimal.ONE;
        if (fromCurrency.trim().equalsIgnoreCase(toCurrency.trim())) return BigDecimal.ONE;
        // simple mock: EUR->BTC ~ 0.00002, BTC->EUR ~ 50000 (inverse)
        if (fromCurrency.equalsIgnoreCase("EUR") && toCurrency.equalsIgnoreCase("BTC")) {
            return new BigDecimal("0.00002");
        }
        if (fromCurrency.equalsIgnoreCase("BTC") && toCurrency.equalsIgnoreCase("EUR")) {
            return new BigDecimal("50000");
        }
        // default fallback
        return BigDecimal.ONE.setScale(18, RoundingMode.HALF_UP);
    }
}

