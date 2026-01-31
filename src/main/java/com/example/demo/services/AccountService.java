package com.example.demo.services;

import com.example.demo.dto.account.AccountResponse;
import com.example.demo.dto.account.CreateAccountRequest;
import com.example.demo.entities.Account;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
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

        // insert into join table if not already linked
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
}
