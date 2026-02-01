package com.example.demo.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class TransactionRepositoryImpl implements TransactionRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Long insertTransaction(String txType, Long fromAcc, Long toAcc, BigDecimal amount, String fromCurrency, String toCurrency, BigDecimal feeAmount, String feeCurrency, BigDecimal rate, String idempotencyKey, String status, String memo) {
        em.createNativeQuery("INSERT INTO transactions (tx_type_key, from_acc_key, to_acc_key, amount, from_currency_code, to_currency_code, fee_amount, fee_currency, exchange_rate, idempotency_key, status, memo) VALUES (:txType, :fromAcc, :toAcc, :amount, :fromCur, :toCur, :fee, :feeCur, :rate, :idem, :status, :memo)")
                .setParameter("txType", txType)
                .setParameter("fromAcc", fromAcc)
                .setParameter("toAcc", toAcc)
                .setParameter("amount", amount)
                .setParameter("fromCur", fromCurrency)
                .setParameter("toCur", toCurrency)
                .setParameter("fee", feeAmount)
                .setParameter("feeCur", feeCurrency)
                .setParameter("rate", rate)
                .setParameter("idem", idempotencyKey)
                .setParameter("status", status)
                .setParameter("memo", memo)
                .executeUpdate();

        // H2: use scope_identity() to get last generated ID in this session/connection
        List<?> rows = em.createNativeQuery("SELECT SCOPE_IDENTITY()").getResultList();
        if (rows.isEmpty()) return null;
        Object val = rows.get(0);
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            return Long.parseLong(val.toString());
        } catch (Exception ex) {
            return null;
        }
    }
}

