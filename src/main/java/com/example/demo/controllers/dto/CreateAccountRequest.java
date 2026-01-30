package com.example.demo.controllers.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateAccountRequest {

    @DecimalMin(value = "0.00", inclusive = true, message = "initialBalance must be >= 0")
    private BigDecimal initialBalance;

    private Boolean active;

    @Size(max = 10)
    @Pattern(regexp = "^[A-Za-z0-9]{1,10}$", message = "currencyCode must be alphanumeric and up to 10 characters")
    private String currencyCode;
    private Boolean cryptoFlag;

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
}

