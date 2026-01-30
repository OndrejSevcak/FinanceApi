package com.example.demo.controllers.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {
    private Long accKey;
    private BigDecimal balance;
    private boolean active;
    private LocalDateTime createdAt;

    public AccountResponse() {
    }

    public AccountResponse(Long accKey, BigDecimal balance, boolean active, LocalDateTime createdAt) {
        this.accKey = accKey;
        this.balance = balance;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getAccKey() {
        return accKey;
    }

    public void setAccKey(Long accKey) {
        this.accKey = accKey;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

