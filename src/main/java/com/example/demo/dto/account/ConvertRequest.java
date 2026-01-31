package com.example.demo.dto.account;

import java.math.BigDecimal;

public class ConvertRequest {
    private Long userKey;
    private Long fromAccKey;
    private Long toAccKey;
    private BigDecimal amount;
    private String idempotencyKey;
    private String note;

    public ConvertRequest() {}

    public Long getUserKey() { return userKey; }
    public void setUserKey(Long userKey) { this.userKey = userKey; }

    public Long getFromAccKey() { return fromAccKey; }
    public void setFromAccKey(Long fromAccKey) { this.fromAccKey = fromAccKey; }

    public Long getToAccKey() { return toAccKey; }
    public void setToAccKey(Long toAccKey) { this.toAccKey = toAccKey; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String idempotencyKey) { this.idempotencyKey = idempotencyKey; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}

