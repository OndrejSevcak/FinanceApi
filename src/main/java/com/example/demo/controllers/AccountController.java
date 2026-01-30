package com.example.demo.controllers;

import com.example.demo.controllers.dto.AccountResponse;
import com.example.demo.controllers.dto.CreateAccountRequest;
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
}

