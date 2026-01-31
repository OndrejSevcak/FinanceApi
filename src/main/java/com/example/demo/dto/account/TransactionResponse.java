package com.example.demo.dto.account;

import java.math.BigDecimal;

public class TransactionResponse {
    private Long txKey;
    private String status;
    private BigDecimal fromBalance;
    private BigDecimal toBalance;

    public TransactionResponse() {}

    public TransactionResponse(Long txKey, String status, BigDecimal fromBalance, BigDecimal toBalance) {
        this.txKey = txKey;
        this.status = status;
        this.fromBalance = fromBalance;
        this.toBalance = toBalance;
    }

    public Long getTxKey() { return txKey; }
    public void setTxKey(Long txKey) { this.txKey = txKey; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getFromBalance() { return fromBalance; }
    public void setFromBalance(BigDecimal fromBalance) { this.fromBalance = fromBalance; }

    public BigDecimal getToBalance() { return toBalance; }
    public void setToBalance(BigDecimal toBalance) { this.toBalance = toBalance; }
}

