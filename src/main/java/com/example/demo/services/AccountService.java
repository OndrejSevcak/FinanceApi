package com.example.demo.services;

import com.example.demo.controllers.dto.AccountResponse;
import com.example.demo.controllers.dto.CreateAccountRequest;
import com.example.demo.entities.Account;
import com.example.demo.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest req) {
        BigDecimal initial = req.getInitialBalance() == null ? BigDecimal.ZERO : req.getInitialBalance();
        boolean active = req.getActive() == null || req.getActive();
        String currencyCode = req.getCurrencyCode() == null ? "EUR" : req.getCurrencyCode();
        Boolean cryptoFlag = req.getCryptoFlag() == null ? Boolean.FALSE : req.getCryptoFlag();

        Account a = new Account();
        a.setBalance(initial);
        a.setActive(active);
        a.setCurrencyCode(currencyCode);
        a.setCryptoFlag(cryptoFlag);

        Account saved = accountRepository.save(a);

        // createdAt is populated by DB
        LocalDateTime createdAt = saved.getCreatedAt();
        if (createdAt == null) {
            Optional<Account> reloaded = accountRepository.findById(saved.getAccKey());
            createdAt = reloaded.map(Account::getCreatedAt).orElse(null);
        }

        return new AccountResponse(saved.getAccKey(), saved.getBalance(), saved.isActive(), createdAt, currencyCode, cryptoFlag);
    }
}

