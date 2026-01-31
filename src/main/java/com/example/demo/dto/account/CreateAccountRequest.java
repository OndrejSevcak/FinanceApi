package com.example.demo.dto.account;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateAccountRequest {

    @DecimalMin(value = "0.00", inclusive = true, message = "initialBalance must be >= 0")
    private BigDecimal initialBalance;

    private Boolean active;

    @Size(max = 10)
    @Pattern(regexp = "^[A-Za-z0-9]{1,10}$", message = "currencyCode must be alphanumeric and up to 10 characters")
    private String currencyCode;
    private Boolean cryptoFlag;

    @NotNull(message = "userKey is required")
    private Long userKey;

    public CreateAccountRequest() {
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Boolean getCryptoFlag() {
        return cryptoFlag;
    }

    public void setCryptoFlag(Boolean cryptoFlag) {
        this.cryptoFlag = cryptoFlag;
    }

    public Long getUserKey() {
        return userKey;
    }

    public void setUserKey(Long userKey) {
        this.userKey = userKey;
    }
}
