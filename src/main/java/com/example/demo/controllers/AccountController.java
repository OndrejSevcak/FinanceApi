package com.example.demo.controllers;

import com.example.demo.dto.account.AccountResponse;
import com.example.demo.dto.account.CreateAccountRequest;
import com.example.demo.dto.account.ConvertRequest;
import com.example.demo.dto.account.TransactionResponse;
import com.example.demo.services.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService)
    {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest req, UriComponentsBuilder uriBuilder) {
        AccountResponse resp = accountService.createAccount(req);
        URI location = uriBuilder.path("/api/accounts/{id}").buildAndExpand(resp.getAccKey()).toUri();
        return ResponseEntity.created(location).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("id") Long accKey) {
        return accountService.getAccountById(accKey)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/convert")
    public ResponseEntity<TransactionResponse> convertAccounts(@Valid @RequestBody ConvertRequest req) {
        Long txKey = accountService.convertBetweenAccounts(req.getUserKey(), req.getFromAccKey(), req.getToAccKey(), req.getAmount(), req.getIdempotencyKey(), req.getNote());
        // fetch balances to return
        java.math.BigDecimal fromBal = accountService.getAccountById(req.getFromAccKey()).map(AccountResponse::getBalance).orElse(null);
        java.math.BigDecimal toBal = accountService.getAccountById(req.getToAccKey()).map(AccountResponse::getBalance).orElse(null);
        TransactionResponse resp = new TransactionResponse(txKey, "COMPLETED", fromBal, toBal);
        return ResponseEntity.ok(resp);
    }
}
