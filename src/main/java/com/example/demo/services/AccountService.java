package com.example.demo.services;

import com.example.demo.dto.account.AccountResponse;
import com.example.demo.dto.account.CreateAccountRequest;
import com.example.demo.entities.Account;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.repositories.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final FxService fxService;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository, FxService fxService, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.fxService = fxService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest req) {
        BigDecimal initial = req.getInitialBalance() == null ? BigDecimal.ZERO : req.getInitialBalance();
        boolean active = req.getActive() == null || req.getActive();
        String currencyCode = req.getCurrencyCode() == null ? "EUR" : req.getCurrencyCode();
        Boolean cryptoFlag = req.getCryptoFlag() == null ? Boolean.FALSE : req.getCryptoFlag();

        // normalize currency code (trim + upper)
        if (currencyCode != null) {
            currencyCode = currencyCode.trim().toUpperCase();
        }

        // verify user exists
        if (req.getUserKey() == null || !userRepository.existsById(req.getUserKey())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // determine account type key
        String accTypeKey = cryptoFlag ? "CRY" : "CUR";

        // prevent more than one currency account per user
        if (!cryptoFlag) {
            int existingCurrency = accountRepository.countUserAccountsByType(req.getUserKey(), "CUR");
            if (existingCurrency > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a currency account");
            }
        }

        // prevent more than one crypto account with same currency for the same user
        if (cryptoFlag) {
            int existingCryptoSameCurrency = accountRepository.countUserCryptoByCurrency(req.getUserKey(), currencyCode);
            if (existingCryptoSameCurrency > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already has a crypto account with this currency");
            }
        }

        Account a = new Account();
        a.setBalance(initial);
        a.setActive(active);
        a.setCurrencyCode(currencyCode);
        a.setCryptoFlag(cryptoFlag);

        Account saved = accountRepository.save(a);

        // insert into user_account table
        int existing = accountRepository.countUserAccount(req.getUserKey(), accTypeKey, saved.getAccKey());
        if (existing == 0) {
            accountRepository.insertUserAccount(req.getUserKey(), accTypeKey, saved.getAccKey());
        }

        // createdAt is populated by DB
        LocalDateTime createdAt = saved.getCreatedAt();
        if (createdAt == null) {
            Optional<Account> reloaded = accountRepository.findById(saved.getAccKey());
            createdAt = reloaded.map(Account::getCreatedAt).orElse(null);
        }

        return new AccountResponse(saved.getAccKey(), saved.getBalance(), saved.isActive(), createdAt, currencyCode, cryptoFlag);
    }

    public Optional<AccountResponse> getAccountById(Long accKey) {
        return accountRepository.findById(accKey)
                .map(a -> new AccountResponse(
                        a.getAccKey(),
                        a.getBalance(),
                        a.isActive(),
                        a.getCreatedAt(),
                        a.getCurrencyCode(),
                        a.getCryptoFlag()));
    }

    @Transactional
    public Long convertBetweenAccounts(Long userKey, Long fromAccKey, Long toAccKey, BigDecimal amount, String idempotencyKey, String note) {
        // basic validations
        if (userKey == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userKey required");
        if (fromAccKey == null || toAccKey == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "account keys required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be positive");

        // verify both accounts belong to user
        int countFrom = accountRepository.countUserAccount(userKey, "CUR", fromAccKey) + accountRepository.countUserAccount(userKey, "CRY", fromAccKey);
        int countTo = accountRepository.countUserAccount(userKey, "CUR", toAccKey) + accountRepository.countUserAccount(userKey, "CRY", toAccKey);
        if (countFrom == 0 || countTo == 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "One or both accounts not owned by user");
        }

        // idempotency: check existing transaction
        if (idempotencyKey != null) {
            Optional<Long> existingTx = transactionRepository.findFirstTxKeyByIdempotencyKey(idempotencyKey);
            if (existingTx.isPresent()) {
                return existingTx.get();
            }
        }

        // load currency codes
        java.util.Optional<String> fromCurrencyOpt = transactionRepository.findCurrencyCodeByAccKey(fromAccKey);
        java.util.Optional<String> toCurrencyOpt = transactionRepository.findCurrencyCodeByAccKey(toAccKey);

        String fromCurrency = fromCurrencyOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account not found"));
        String toCurrency = toCurrencyOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target account not found"));

        // get FX rate
        BigDecimal rate = fxService.getRate(fromCurrency, toCurrency);
        if (rate == null) rate = BigDecimal.ONE;

        // define fee percent (e.g., 0.5% = 0.005)
        BigDecimal feePercent = new BigDecimal("0.005");
        BigDecimal fee = amount.multiply(feePercent).setScale(18, RoundingMode.HALF_UP);
        BigDecimal debitTotal = amount.add(fee);

        // attempt atomic debit from source
        int updated = accountRepository.debitIfSufficient(fromAccKey, debitTotal);
        if (updated == 0) {
            // insufficient funds
            // insert failed transaction record
            transactionRepository.insertTransaction("CONVERSION", fromAccKey, toAccKey, amount, fromCurrency, toCurrency, fee, fromCurrency, rate, idempotencyKey, "FAILED", note);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
        }

        // credit target with converted amount
        BigDecimal toAmount = amount.multiply(rate).setScale(18, RoundingMode.HALF_UP);
        accountRepository.credit(toAccKey, toAmount);

        // insert successful transaction
        Long txKey = transactionRepository.insertTransaction("CONVERSION", fromAccKey, toAccKey, amount, fromCurrency, toCurrency, fee, fromCurrency, rate, idempotencyKey, "COMPLETED", note);

        return txKey;
    }
}
