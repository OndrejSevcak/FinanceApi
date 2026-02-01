package com.example.demo.repositories;

import java.math.BigDecimal;

public interface TransactionRepositoryCustom {
    Long insertTransaction(String txType, Long fromAcc, Long toAcc, BigDecimal amount, String fromCurrency, String toCurrency, BigDecimal feeAmount, String feeCurrency, BigDecimal rate, String idempotencyKey, String status, String memo);
}

