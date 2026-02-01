package com.example.demo.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tx_key")
    private Long txKey;

    @Column(name = "tx_type_key", nullable = false)
    private String txTypeKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_acc_key")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_acc_key")
    private Account toAccount;

    @Column(name = "amount", precision = 36, scale = 18, nullable = false)
    private BigDecimal amount;

    @Column(name = "from_currency_code")
    private String fromCurrencyCode;

    @Column(name = "to_currency_code")
    private String toCurrencyCode;

    @Column(name = "fee_amount", precision = 36, scale = 18)
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Column(name = "fee_currency")
    private String feeCurrency;

    @Column(name = "exchange_rate", precision = 36, scale = 18)
    private BigDecimal exchangeRate;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "chain")
    private String chain;

    @Column(name = "confirmations")
    private Integer confirmations;

    @Column(name = "confirmations_required")
    private Integer confirmationsRequired = 0;

    @Column(name = "status")
    private String status = "PENDING";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @Column(name = "reference")
    private String reference;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    public Transaction() {
    }

    public Long getTxKey() {
        return txKey;
    }

    public void setTxKey(Long txKey) {
        this.txKey = txKey;
    }

    public String getTxTypeKey() {
        return txTypeKey;
    }

    public void setTxTypeKey(String txTypeKey) {
        this.txTypeKey = txTypeKey;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Account fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public void setToAccount(Account toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFromCurrencyCode() {
        return fromCurrencyCode;
    }

    public void setFromCurrencyCode(String fromCurrencyCode) {
        this.fromCurrencyCode = fromCurrencyCode;
    }

    public String getToCurrencyCode() {
        return toCurrencyCode;
    }

    public void setToCurrencyCode(String toCurrencyCode) {
        this.toCurrencyCode = toCurrencyCode;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getFeeCurrency() {
        return feeCurrency;
    }

    public void setFeeCurrency(String feeCurrency) {
        this.feeCurrency = feeCurrency;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

    public Integer getConfirmationsRequired() {
        return confirmationsRequired;
    }

    public void setConfirmationsRequired(Integer confirmationsRequired) {
        this.confirmationsRequired = confirmationsRequired;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
